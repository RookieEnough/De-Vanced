/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/tree/main/patches/src/main/kotlin/app/revanced/patches/lightroom
 */
package app.morphe.patches.lightroom.misc.login

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly

@Suppress("unused")
val disableMandatoryLoginPatch = bytecodePatch(
    name = "Disable mandatory login",
    description = "Disables the mandatory login requirement, allowing the app to be used without signing in.",
) {
    compatibleWith(AppCompatibilities.LIGHTROOM)

    execute {
        // Force isLoggedIn = true.
        IsLoggedInMethodFingerprint.method.returnEarly(true)
    }
}
