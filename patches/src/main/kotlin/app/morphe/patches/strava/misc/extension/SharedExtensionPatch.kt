/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/strava/misc/extension/SharedExtensionPatch.kt
 */
package app.morphe.patches.strava.misc.extension

import app.morphe.patcher.Fingerprint
import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch

internal object StravaApplicationOnCreateFingerprint : Fingerprint(
    custom = { method, classDef ->
        method.name == "onCreate" && classDef.endsWith("/StravaApplication;")
    },
)

internal val applicationOnCreateHook = ExtensionHook(
    fingerprint = StravaApplicationOnCreateFingerprint,
)

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "strava",
    isGmsCorePatch = false,
    applicationOnCreateHook,
)

