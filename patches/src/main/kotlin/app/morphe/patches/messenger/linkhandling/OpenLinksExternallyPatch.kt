package app.morphe.patches.messenger.linkhandling

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val openLinksExternallyPatch = bytecodePatch(
    name = "Open links externally",
    description = "Always opens links in your default browser instead of the in-app browser, " +
        "regardless of the in-app setting.",
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    execute {
        // Force the in-app-browser decision to always skip the in-app browser,
        // which is exactly what enabling the app's "Open links in external browser" setting does.
        ShouldOpenInAppBrowserFingerprint.method.returnEarly(false)
    }
}
