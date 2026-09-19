/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/amznmusic/misc/extension/ExtensionPatch.kt
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/amznmusic/misc/extension/Hooks.kt
 */

/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/amznmusic/misc/extension/SharedExtensionPatch.kt
 */
package app.morphe.patches.amznmusic.misc.extension

import app.morphe.patcher.Fingerprint
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

internal object MusicHomeActivityOnCreateFingerprint : Fingerprint(
    custom = { method, classDef ->
        method.name == "onCreate" && classDef.endsWith("/MusicHomeActivity;")
    },
)

private val applicationInitHook = ExtensionHook(
    fingerprint = MusicHomeActivityOnCreateFingerprint,
)

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "amznmusic",
    isGmsCorePatch = false,
    applicationInitHook,
)

