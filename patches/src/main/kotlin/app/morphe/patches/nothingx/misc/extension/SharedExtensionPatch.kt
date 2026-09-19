/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/nothingx/misc/extension/SharedExtensionPatch.kt
 */
package app.morphe.patches.nothingx.misc.extension

import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch
import app.morphe.patcher.Fingerprint

private val applicationOnCreateHook = ExtensionHook(
    fingerprint = Fingerprint(
        definingClass = "/BaseApplication;",
        name = "onCreate",
        returnType = "V",
        parameters = listOf(),
    ),
)

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "nothingx",
    isGmsCorePatch = false,
    applicationOnCreateHook,
)

