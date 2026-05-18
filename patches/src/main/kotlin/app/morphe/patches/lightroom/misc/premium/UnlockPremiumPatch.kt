/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/tree/main/patches/src/main/kotlin/app/revanced/patches/lightroom
 */
package app.morphe.patches.lightroom.misc.premium

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val unlockPremiumPatch = bytecodePatch(
    name = "Unlock Premium",
    description = "Unlocks premium features by making the premium check always return true.",
) {
    compatibleWith(AppCompatibilities.LIGHTROOM)

    execute {
        // Set hasPremium = true.
        HasPurchasedMethodFingerprint.method.returnEarly(true)
    }
}
