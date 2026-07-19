package app.morphe.patches.messenger.linkhandling

import app.morphe.patcher.Fingerprint

/**
 * Matches the method that decides whether a URL should open in the in-app browser.
 * It returns `true` to use the in-app browser and `false` to skip it (open externally);
 * one of its skip reasons is `user_prefers_external`, logged under `iab_skipped_reason`.
 */
internal object ShouldOpenInAppBrowserFingerprint : Fingerprint(
    parameters = listOf("Landroid/net/Uri;", "Lcom/facebook/auth/usersession/FbUserSession;"),
    returnType = "Z",
    strings = listOf("iab_skipped_reason", "user_prefers_external"),
)
