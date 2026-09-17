/*
 * Copyright 2026 De-Vanced
 * https://github.com/RookieEnough/De-Vanced
 *
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/HideAdsPatch.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.util.returnEarly

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
) {
    compatibleWith(AppCompatibilities.PIXIV_ADS)

    execute {
        if (packageMetadata.versionName == "6.141.1") {
            ShouldShowAdsLegacyFingerprint.method.returnEarly(false)
        } else {
            // The central ads gate (a no-arg method returning Z, called right after
            // the interstitial readiness check) is resolved dynamically so no
            // obfuscated names are hardcoded. It must be unique: anything else
            // means the app was refactored and the patch needs revisiting.
            val candidates = ShouldShowAdsFingerprint.instructionMatches
                .map { it.getMethodCalled() }
                .filter {
                    it.returnType == "Z" && it.parameters.isEmpty() &&
                        !it.definingClass.contains("applovin")
                }
            if (candidates.size != 1) throw PatchException("Ads gate resolution failed")

            candidates.first().returnEarly(false)
        }
    }
}

