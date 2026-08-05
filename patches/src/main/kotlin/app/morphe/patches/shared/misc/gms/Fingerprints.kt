/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/shared/misc/gms/Fingerprints.kt
 */
package app.morphe.patches.shared.misc.gms

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object GooglePlayUtilityFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "I",
    parameters = listOf("L", "I"),
    strings = listOf(
        "This should never happen.",
        "MetadataValueReader",
        "com.google.android.gms",
    )
)

/**
 * Matches `GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(Context, int)`.
 *
 * Google Photos uses this bundled availability check while restoring the active account. The
 * check rejects GmsCore's non-Google signature with `SERVICE_INVALID`, even though GmsCore has
 * already returned the account successfully.
 *
 * Ported from ReVanced commit 7657187976d0a78df88fe5676632ef706f4924e4.
 */
internal object IsGooglePlayServicesAvailableFingerprint : Fingerprint(
    returnType = "I",
    parameters = listOf("L", "I"),
    strings = listOf(
        "com.google.android.gms.version",
        "com.google.app.id",
    ),
)

internal object ServiceCheckFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("L", "I"),
    strings = listOf("Google Play Services not available")
)

internal object GmsCoreSupportFingerprint : Fingerprint(
    definingClass = EXTENSION_CLASS_DESCRIPTOR,
    name = "getGmsCoreVendorGroupId",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.STATIC),
    returnType = "Ljava/lang/String;",
    parameters = listOf()
)

internal object OriginalPackageNameExtensionFingerprint : Fingerprint(
    definingClass = EXTENSION_CLASS_DESCRIPTOR,
    name = "getOriginalPackageName",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.STATIC),
    returnType = "Ljava/lang/String;",
    parameters = listOf()
)

internal object MapViewInitFingerprint : Fingerprint(
    definingClass = "Lcom/google/android/gms/maps/MapView;",
    name = "c",
    returnType = "V",
    parameters = listOf(),
)

