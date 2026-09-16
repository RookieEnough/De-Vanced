/*
 * Copyright 2026 De-Vanced
 * https://github.com/RookieEnough/De-Vanced
 *
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/Fingerprints.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object ShouldShowAdsLegacyFingerprint : Fingerprint(
    definingClass = "/AdUtils;",
    name = "shouldShowAds",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
)

/**
 * Matches the central ads gate on 6.196.0+.
 *
 * This is the direct successor of `AdUtils.shouldShowAds()` from 6.141.1 (same semantics:
 * "not a premium user") and is consulted at every ad decision site: in-feed rectangle and
 * self-serve insertion (tx4), novel feed ads (ux4), artwork detail ads (em4), ad-slot
 * position calculators (pm4/gs5), overlay banner show/hide (tl7) and interstitials
 * (fy/ey). Forcing it to false removes ad items at insertion, so no placeholders remain.
 */
internal object ShouldShowAdsGateFingerprint : Fingerprint(
    definingClass = "Lzc;",
    name = "a",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
)
