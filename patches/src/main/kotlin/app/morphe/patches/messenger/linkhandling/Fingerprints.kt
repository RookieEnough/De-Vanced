package app.morphe.patches.messenger.linkhandling

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the method that returns why a URL should skip the in-app browser.
 * A `null` result opens the in-app browser, any reason opens the link externally.
 * One of its reasons is `user_prefers_external`, set by the app's "Open links in external browser" setting.
 */
internal object GetInAppBrowserSkipReasonFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "Ljava/lang/String;",
    parameters = listOf("Landroid/net/Uri;", "Lcom/facebook/auth/usersession/FbUserSession;", "L"),
    strings = listOf("no_capability", "user_prefers_external"),
)
