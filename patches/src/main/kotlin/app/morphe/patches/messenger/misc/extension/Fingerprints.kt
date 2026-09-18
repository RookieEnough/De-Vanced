/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/misc/extension/Fingerprints.kt
 */
package app.morphe.patches.messenger.misc.extension

import app.morphe.patcher.Fingerprint
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

internal object MessengerApplicationOnCreateFingerprint : Fingerprint(
    custom = { method, classDef ->
        method.name == "onCreate" && classDef.endsWith("/MessengerApplication;")
    },
)

internal val messengerApplicationOnCreateHook = ExtensionHook(
    fingerprint = MessengerApplicationOnCreateFingerprint,
)

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "messenger",
    isGmsCorePatch = false,
    messengerApplicationOnCreateHook,
)
