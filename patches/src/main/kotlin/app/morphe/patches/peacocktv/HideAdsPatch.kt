/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/peacocktv/ads/HideAdsPatch.kt
 */
package app.morphe.patches.peacocktv

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Hides all video ads.",
) {
    compatibleWith(AppCompatibilities.PEACOCK_TV)

    execute {
        // Target method returns Object; return null to abort service construction.
        MediaTailerAdServiceMethodFingerprint.method.returnEarly(null)
    }
}
