package app.morphe.extension.shared.patches;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.Utils;

/**
 * Supplies the active Google account avatar to Google Photos' OneGoogle UI.
 *
 * <p>GmsCore stores and displays the avatar itself, but the modern GOOGLE_AUTH_AANG/INAPP_REACH
 * APIs used by Photos do not currently return it. Photos consequently renders the account's first
 * letter even though authentication works. This bridge uses the existing GmsCore account token,
 * downloads the standard Google user-info picture, caches it locally, and applies it to both the
 * toolbar account disc and the expanded account sheet.</p>
 */
final class GooglePhotosAccountAvatar {
    private static final String ACCOUNT_TYPE = "app.revanced";
    private static final String PROFILE_TOKEN_TYPE =
            "oauth2:openid https://www.googleapis.com/auth/mobileapps.native "
                    + "https://www.googleapis.com/auth/photos.native";
    private static final String USER_INFO_URL =
            "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String CACHE_FILE_NAME = "google_account_profile_avatar.png";
    private static final long CACHE_MAX_AGE_MILLIS = 6L * 60L * 60L * 1000L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}",
            Pattern.CASE_INSENSITIVE
    );
    private static final String ACCOUNT_AVATAR_OVERLAY_TAG =
            "morphe_google_photos_account_avatar";

    private static final AtomicBoolean FETCH_STARTED = new AtomicBoolean();
    private static final Set<ImageView> SCHEDULED_TOOLBAR_AVATARS =
            Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<View> SCHEDULED_ACCOUNT_SHEETS =
            Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<View> OBSERVED_WINDOW_ROOTS =
            Collections.newSetFromMap(new WeakHashMap<>());
    private static final AtomicBoolean WINDOW_SCAN_STARTED = new AtomicBoolean();
    private static final AtomicBoolean WINDOW_SCAN_ERROR_LOGGED = new AtomicBoolean();

    @Nullable
    private static volatile Bitmap avatar;

    private GooglePhotosAccountAvatar() {
    }

    static void install(Activity activity) {
        Logger.printInfo(() -> "Installing the Google Photos account avatar bridge");
        View root = activity.getWindow().getDecorView();
        observeWindowRoot(activity, root);
        startWindowRootScan(activity);

        if (avatar == null) {
            avatar = readCachedAvatar(activity);
        }
        applyAvatar(activity, root);

        if (avatar == null) {
            requestProfileToken(activity, root);
        }
    }

    private static void observeWindowRoot(Activity activity, View root) {
        if (!OBSERVED_WINDOW_ROOTS.add(root)) return;

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            applyAvatar(activity, root);
            if (avatar == null && !FETCH_STARTED.get()) {
                requestProfileToken(activity, root);
            }
        });
        applyAvatar(activity, root);
    }

    private static void startWindowRootScan(Activity activity) {
        if (!WINDOW_SCAN_STARTED.compareAndSet(false, true)) return;

        Utils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isDestroyed()) {
                    WINDOW_SCAN_STARTED.set(false);
                    return;
                }

                try {
                    Class<?> windowManagerGlobalClass =
                            Class.forName("android.view.WindowManagerGlobal");
                    Object windowManagerGlobal = windowManagerGlobalClass
                            .getMethod("getInstance")
                            .invoke(null);
                    java.lang.reflect.Field viewsField =
                            windowManagerGlobalClass.getDeclaredField("mViews");
                    viewsField.setAccessible(true);
                    Object roots = viewsField.get(windowManagerGlobal);
                    if (roots instanceof List<?>) {
                        for (Object root : (List<?>) roots) {
                            if (root instanceof View) observeWindowRoot(activity, (View) root);
                        }
                    }
                } catch (Exception exception) {
                    if (WINDOW_SCAN_ERROR_LOGGED.compareAndSet(false, true)) {
                        Logger.printException(
                                () -> "Could not inspect Google Photos account-panel windows",
                                exception
                        );
                    }
                }

                Utils.runOnMainThreadDelayed(this, 500);
            }
        });
    }

    private static void requestProfileToken(Activity activity, View root) {
        try {
            AccountManager accountManager = AccountManager.get(activity);
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            Account account = accounts.length == 0
                    ? findSelectedAccount(activity, root)
                    : accounts[0];
            if (account == null || !FETCH_STARTED.compareAndSet(false, true)) return;

            Logger.printInfo(() -> accounts.length == 0
                    ? "Requesting the avatar token for the selected Photos account"
                    : "Requesting the avatar token for a visible GmsCore account");

            Bundle options = new Bundle();
            options.putString("androidPackageName", activity.getPackageName());
            AccountManagerFuture<Bundle> future = accountManager.getAuthToken(
                    account,
                    PROFILE_TOKEN_TYPE,
                    options,
                    false,
                    null,
                    null
            );
            Utils.runOnBackgroundThread(() -> {
                try {
                    Bundle result = future.getResult();
                    String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                    if (token == null || token.isEmpty()) {
                        throw new IllegalStateException("GmsCore returned no profile token");
                    }

                    Bitmap downloadedAvatar = downloadAvatar(token);
                    if (downloadedAvatar == null) {
                        throw new IllegalStateException("Google user-info returned no avatar");
                    }

                    avatar = downloadedAvatar;
                    writeCachedAvatar(activity, downloadedAvatar);
                    Logger.printInfo(() -> "Google Photos account avatar loaded");
                    Utils.runOnMainThread(() -> applyAvatar(activity, root));
                } catch (Exception exception) {
                    Logger.printException(
                            () -> "Could not load the Google Photos account avatar",
                            exception
                    );
                    FETCH_STARTED.set(false);
                }
            });
        } catch (Exception exception) {
            Logger.printException(
                    () -> "Could not request the Google Photos profile token",
                    exception
            );
            FETCH_STARTED.set(false);
        }
    }

    @Nullable
    private static Account findSelectedAccount(Activity activity, View root) {
        int selectedAccountId = activity.getResources().getIdentifier(
                "selected_account_disc", "id", activity.getPackageName());
        if (selectedAccountId == 0) return null;

        View selectedAccount = root.findViewById(selectedAccountId);
        if (selectedAccount == null || selectedAccount.getContentDescription() == null) return null;

        Matcher matcher = EMAIL_PATTERN.matcher(selectedAccount.getContentDescription());
        return matcher.find() ? new Account(matcher.group(), ACCOUNT_TYPE) : null;
    }

    @Nullable
    private static Bitmap downloadAvatar(String token) throws Exception {
        HttpURLConnection userInfoConnection = openConnection(USER_INFO_URL);
        userInfoConnection.setRequestProperty("Authorization", "Bearer " + token);

        try {
            int status = userInfoConnection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("Google user-info HTTP status " + status);
            }

            JsonObject response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    userInfoConnection.getInputStream(), StandardCharsets.UTF_8))) {
                response = JsonParser.parseReader(reader).getAsJsonObject();
            }

            JsonElement picture = response.get("picture");
            if (picture == null || picture.isJsonNull()) return null;

            HttpURLConnection imageConnection = openConnection(picture.getAsString());
            try {
                if (imageConnection.getResponseCode() != HttpURLConnection.HTTP_OK) return null;
                try (InputStream stream = new BufferedInputStream(imageConnection.getInputStream())) {
                    return getCircularBitmap(BitmapFactory.decodeStream(stream));
                }
            } finally {
                imageConnection.disconnect();
            }
        } finally {
            userInfoConnection.disconnect();
        }
    }

    private static HttpURLConnection openConnection(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(15_000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Accept", "application/json,image/*");
        return connection;
    }

    private static void applyAvatar(Activity activity, View root) {
        Bitmap currentAvatar = avatar;
        if (currentAvatar == null || activity.isFinishing() || activity.isDestroyed()) return;

        int toolbarAvatarId = activity.getResources().getIdentifier(
                "og_apd_internal_image_view", "id", activity.getPackageName());
        if (toolbarAvatarId != 0) {
            View toolbarAvatar = root.findViewById(toolbarAvatarId);
            if (toolbarAvatar instanceof ImageView) {
                updateImageView((ImageView) toolbarAvatar, currentAvatar);
            }
        }

        int accountSheetAvatarId = activity.getResources().getIdentifier(
                "og_bento_selected_account_avatar", "id", activity.getPackageName());
        if (accountSheetAvatarId != 0) {
            View accountSheetAvatar = root.findViewById(accountSheetAvatarId);
            applyAccountSheetAvatar(accountSheetAvatar, currentAvatar);
            if (accountSheetAvatar != null && SCHEDULED_ACCOUNT_SHEETS.add(accountSheetAvatar)) {
                // OneGoogle fills the large avatar asynchronously after the sheet is laid out.
                // Reapply after those updates so its generated initial cannot overwrite the image.
                accountSheetAvatar.postDelayed(
                        () -> applyAccountSheetAvatar(accountSheetAvatar, currentAvatar), 100);
                accountSheetAvatar.postDelayed(
                        () -> applyAccountSheetAvatar(accountSheetAvatar, currentAvatar), 400);
                accountSheetAvatar.postDelayed(
                        () -> applyAccountSheetAvatar(accountSheetAvatar, currentAvatar), 1_200);
            }
        }
    }

    @Nullable
    private static Bitmap getCircularBitmap(@Nullable Bitmap src) {
        if (src == null) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        Rect srcRect = new Rect((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);
        RectF dstRect = new RectF(0, 0, size, size);

        canvas.drawRoundRect(dstRect, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, srcRect, dstRect, paint);

        return output;
    }

    private static void applyAccountSheetAvatar(@Nullable View accountSheetAvatar, Bitmap bitmap) {
        if (!(accountSheetAvatar instanceof ViewGroup)) return;

        ViewGroup group = (ViewGroup) accountSheetAvatar;
        ImageView targetImageView = findTargetImageView(group);
        if (targetImageView != null) {
            targetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            targetImageView.setImageBitmap(bitmap);
        } else {
            ImageView overlay = null;
            for (int index = 0; index < group.getChildCount(); index++) {
                View child = group.getChildAt(index);
                if (ACCOUNT_AVATAR_OVERLAY_TAG.equals(child.getTag()) && child instanceof ImageView) {
                    overlay = (ImageView) child;
                    break;
                }
            }

            if (overlay == null) {
                overlay = new ImageView(group.getContext());
                overlay.setTag(ACCOUNT_AVATAR_OVERLAY_TAG);
                overlay.setScaleType(ImageView.ScaleType.CENTER_CROP);
                group.addView(
                        overlay,
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        )
                );
            }
            overlay.setImageBitmap(bitmap);
        }
    }

    @Nullable
    private static ImageView findTargetImageView(ViewGroup group) {
        for (int index = 0; index < group.getChildCount(); index++) {
            View child = group.getChildAt(index);
            if (child instanceof ImageView
                    && !ACCOUNT_AVATAR_OVERLAY_TAG.equals(child.getTag())) {
                return (ImageView) child;
            }
        }
        return null;
    }

    private static void updateImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        if (SCHEDULED_TOOLBAR_AVATARS.add(imageView)) {
            imageView.postDelayed(() -> setImageBitmap(imageView, bitmap), 100);
            imageView.postDelayed(() -> setImageBitmap(imageView, bitmap), 500);
            imageView.postDelayed(() -> setImageBitmap(imageView, bitmap), 1_500);
            imageView.postDelayed(() -> setImageBitmap(imageView, bitmap), 5_000);
            imageView.postDelayed(() -> setImageBitmap(imageView, bitmap), 12_000);
        }
    }

    private static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (imageView.isAttachedToWindow()) imageView.setImageBitmap(bitmap);
    }

    @Nullable
    private static Bitmap readCachedAvatar(Activity activity) {
        File cacheFile = new File(activity.getCacheDir(), CACHE_FILE_NAME);
        if (!cacheFile.isFile()) return null;
        if (System.currentTimeMillis() - cacheFile.lastModified() > CACHE_MAX_AGE_MILLIS) return null;

        try (InputStream stream = new FileInputStream(cacheFile)) {
            return getCircularBitmap(BitmapFactory.decodeStream(stream));
        } catch (Exception exception) {
            Logger.printException(() -> "Could not read the cached Google account avatar", exception);
            return null;
        }
    }

    private static void writeCachedAvatar(Activity activity, Bitmap bitmap) {
        File cacheFile = new File(activity.getCacheDir(), CACHE_FILE_NAME);
        try (FileOutputStream output = new FileOutputStream(cacheFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        } catch (Exception exception) {
            Logger.printException(() -> "Could not cache the Google account avatar", exception);
        }
    }
}
