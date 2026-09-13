/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/googlephotos/misc/gms/Fingerprints.kt
 */
package app.morphe.patches.googlephotos.misc.gms

import app.morphe.patcher.Fingerprint
import app.morphe.patches.googlephotos.misc.gms.Constants.PHOTOS_MAIN_ACTIVITY_CLASS_TYPE

internal object HomeActivityOnCreateFingerprint : Fingerprint(
    definingClass = PHOTOS_MAIN_ACTIVITY_CLASS_TYPE,
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
)

/**
 * Matches GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(Context, int).
 *
 * The bundled check rejects GmsCore's non-Google signature with SERVICE_INVALID even after
 * account authentication succeeds. Match the method using the stable availability-check anchor
 * also used by the upstream ReVanced fix instead of relying on any obfuscated method name.
 */
internal object IsGooglePlayServicesAvailableFingerprint : Fingerprint(
    returnType = "I",
    parameters = listOf("L", "I"),
    strings = listOf(
        "com.google.android.gms.version",
        "com.google.app.id",
    ),
)
