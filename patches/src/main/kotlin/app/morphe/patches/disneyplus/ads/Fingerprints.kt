/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/disneyplus/ads/Fingerprints.kt
 */
package app.morphe.patches.disneyplus.ads

import app.morphe.patcher.Fingerprint

internal object InsertionGetPointsFingerprint : Fingerprint(
    definingClass = "Lcom/dss/sdk/internal/media/Insertion;",
    name = "getPoints",
    returnType = "Ljava/util/List"
)

internal object InsertionGetRangesFingerprint : Fingerprint(
    definingClass = "Lcom/dss/sdk/internal/media/Insertion;",
    name = "getRanges",
    returnType = "Ljava/util/List"
)
