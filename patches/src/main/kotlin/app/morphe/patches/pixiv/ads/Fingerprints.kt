/*
 * Copyright 2026 De-Vanced
 * https://github.com/RookieEnough/De-Vanced
 *
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/pixiv/ads/Fingerprints.kt
 */
package app.morphe.patches.pixiv.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterWithin
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object ShouldShowAdsLegacyFingerprint : Fingerprint(
    definingClass = "/AdUtils;",
    name = "shouldShowAds",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
)

internal object ShouldShowAdsFingerprint : Fingerprint(
    name = "invokeSuspend",
    filters = listOf(
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            smali = "Lcom/applovin/mediation/ads/MaxInterstitialAd;->isReady()Z"
        ),
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            returnType = "Z",
            parameters = listOf(),
            location = MatchAfterWithin(20)
        )
    )
)
