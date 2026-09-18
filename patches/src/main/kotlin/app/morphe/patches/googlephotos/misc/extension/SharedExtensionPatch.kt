/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/googlephotos/misc/extension/SharedExtensionPatch.kt
 */
package app.morphe.patches.googlephotos.misc.extension

import app.morphe.patches.googlephotos.misc.gms.HomeActivityOnCreateFingerprint
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

private class HomeActivityInitHook : ExtensionHook(
    fingerprint = HomeActivityOnCreateFingerprint,
    insertIndexResolver = { 0 },
    contextRegisterResolver = { "p0" },
)

internal val homeActivityInitHook: ExtensionHook = HomeActivityInitHook()

val sharedExtensionPatch = sharedExtensionPatch(
    isGmsCorePatch = true,
    homeActivityInitHook,
)

