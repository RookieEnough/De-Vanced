/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/Fingerprints.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object ShouldShowAdsFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf(),
    custom = { methodDef, classDef ->
        // Pixiv 6.196.0 obfuscates AdUtils to the default-package class `zc`
        // and renames shouldShowAds() to a(). Keep the original signature so
        // the patch remains usable with the previously supported release.
        (classDef.type.endsWith("AdUtils;") && methodDef.name == "shouldShowAds") ||
            (classDef.type == "Lzc;" && methodDef.name == "a")
    },
)

