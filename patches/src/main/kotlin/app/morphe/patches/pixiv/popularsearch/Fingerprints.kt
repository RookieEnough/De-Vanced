/*
 * Copyright 2025 De-Vanced.
 * https://github.com/RookieEnough/De-Vanced
 */

package app.morphe.patches.pixiv.popularsearch

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.opcode
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object PremiumTrialServiceGetPremiumTrialExpireDaysLegacyFingerprint : Fingerprint(
    definingClass = "/PremiumTrialService;",
    name = "getPremiumTrialExpireDays",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "I",
    filters = listOf(
        opcode(Opcode.RSUB_INT_LIT8)
    )
)

/**
 * Matches the trial-countdown site on 6.196.0.
 *
 * There `PremiumTrialService.getPremiumTrialExpireDays()` no longer exists: the same
 * `7 - daysSinceFirstLaunch` computation was inlined into the (R8-obfuscated) search-result
 * trial fragment method, so it is matched structurally. The anchor string is the Hilt
 * null-check literal of the injected `computePremiumTrialExpireDaysUseCase` field, which
 * occurs exactly once in the whole app, inside the target method that also holds a single
 * `RSUB_INT_LIT8` (`rsub-int/lit8 vX, vX, 0x7`).
 */
internal object ComputePremiumTrialExpireDaysFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    strings = listOf("computePremiumTrialExpireDaysUseCase"),
    filters = listOf(
        opcode(Opcode.RSUB_INT_LIT8)
    )
)

/**
 * Matches the trial-page routing site on 6.196.0 (the result pager adapter method that
 * instantiates the trial fragment or, once expired, the preview page).
 *
 * Like the countdown above, it recomputes `7 - daysSinceFirstLaunch` inline, so without
 * patching it the trial page is never reached after 7 days. Fully structural: among the
 * search-result fragments sharing the "SEARCH_PARAMETER" bundle key, only this method
 * (returning a Fragment) and the countdown method (returning void) contain `RSUB_INT_LIT8`.
 */
internal object SearchResultTrialGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Landroidx/fragment/app/Fragment;",
    parameters = listOf("I"),
    strings = listOf("SEARCH_PARAMETER"),
    filters = listOf(
        opcode(Opcode.RSUB_INT_LIT8)
    )
)
