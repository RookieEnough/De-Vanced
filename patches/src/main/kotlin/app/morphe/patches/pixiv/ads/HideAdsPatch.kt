/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/HideAdsPatch.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
) {
    compatibleWith(AppCompatibilities.PIXIV)

    execute {
        ShouldShowAdsFingerprint.method.returnEarly(false)
    }
}

