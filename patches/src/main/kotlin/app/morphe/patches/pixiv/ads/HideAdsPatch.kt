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
    compatibleWith(AppCompatibilities.PIXIV_ADS)

    execute {
        val method = if (packageMetadata.versionName == "6.141.1") {
            ShouldShowAdsLegacyFingerprint.method
        } else {
            ShouldShowAdsFingerprint.instructionMatches.first().getMethodCalled()
        }

        method.returnEarly(false)
    }
}

