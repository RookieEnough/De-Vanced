/*
 * Copyright 2026 De-Vanced
 * https://github.com/RookieEnough/De-Vanced
 *
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/HideAdsPatch.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.util.returnEarly

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
) {
    compatibleWith(AppCompatibilities.PIXIV)

    execute {
        if (packageMetadata.versionName == "6.141.1") {
            ShouldShowAdsLegacyFingerprint.method.returnEarly(false)
        } else {
            // Instruction matches follow the fingerprint filters one-to-one, so index 1
            // is the app-level no-arg Z-returning call (the ads gate), resolved
            // dynamically so no obfuscated names are hardcoded.
            ShouldShowAdsFingerprint.instructionMatches[1].getMethodCalled().returnEarly(false)
        }
    }
}

