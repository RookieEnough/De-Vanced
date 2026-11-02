/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/strava/media/upload/OverwriteMediaUploadParametersPatch.kt
 */
package app.morphe.patches.strava.media.upload

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.intOption
import app.morphe.patcher.patch.longOption
import app.morphe.util.returnEarly

@Suppress("unused")
val overwriteMediaUploadParametersPatch = bytecodePatch(
    name = "Overwrite media upload parameters",
    description = "Overwrites the compression, resize and trim media (images and videos) parameters returned by Strava's server before upload.",
) {
    compatibleWith(AppCompatibilities.STRAVA)

    val compressionQuality by intOption(
        key = "compressionQuality",
        title = "Compression quality (percent)",
        description = "This is used as the JPEG quality setting (≤ 100).",
    ) { it == null || it in 1..100 }

    val maxDuration by longOption(
        key = "maxDuration",
        title = "Max duration (seconds)",
        description = "The maximum length (≤ ${60 * 60}) of a video before it gets trimmed.",
    ) { it == null || it in 1..60 * 60 }

    val maxSize by intOption(
        key = "maxSize",
        title = "Max size (pixels)",
        description = "The image gets resized so that the smaller dimension (width/height) does not exceed this value (≤ 10000).",
    ) { it == null || it in 1..10000 }

    execute {
        val mediaUploadParametersClass = classDefBy {
            classDef -> classDef.type.endsWith("/MediaUploadParameters;")
        }

        compressionQuality?.let { compressionQuality ->
            GetCompressionQualityFingerprint.match(mediaUploadParametersClass).method
                .returnEarly(compressionQuality / 100f)
        }

        maxDuration?.let { maxDuration ->
            GetMaxDurationFingerprint.match(mediaUploadParametersClass).method.returnEarly(maxDuration)
        }

        maxSize?.let { maxSize ->
            GetMaxSizeFingerprint.match(mediaUploadParametersClass).method.returnEarly(maxSize)
        }
    }
}
