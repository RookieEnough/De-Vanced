/*
 * Copyright 2025 De-Vanced.
 * https://github.com/RookieEnough/De-Vanced
 */

package app.morphe.patches.pixiv.popularsearch

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
val removePopularSearchTimeLimitPatch = bytecodePatch(
    name = "Remove popular search time limit",
    description = "Removes the 7-day trial countdown on popular search results so the free " +
        "popular-search preview (30 works) never expires.",
) {
    compatibleWith(AppCompatibilities.PIXIV)

    execute {
        // 6.141.1 computes the remaining days in
        // PremiumTrialService.getPremiumTrialExpireDays(); 6.196.0 inlined the same
        // `7 - daysSinceFirstLaunch` computation into the trial fragment method
        // (countdown display) and the result pager adapter (routing between the
        // trial page and the preview page), so each version needs its own
        // fingerprint (same as HideAdsPatch does).
        fun pinTrialCountdownToSevenDays(fingerprint: Fingerprint) {
            fingerprint.method.apply {
                val daysSinceFirstLaunchSubIndex = fingerprint.instructionMatches.first().index
                val register = getInstruction<OneRegisterInstruction>(daysSinceFirstLaunchSubIndex).registerA

                replaceInstruction(
                    daysSinceFirstLaunchSubIndex,
                    "const/4 v$register, 0x7"
                )
            }
        }

        if (packageMetadata.versionName == "6.141.1") {
            pinTrialCountdownToSevenDays(PremiumTrialServiceGetPremiumTrialExpireDaysLegacyFingerprint)
        } else {
            pinTrialCountdownToSevenDays(ComputePremiumTrialExpireDaysFingerprint)
            pinTrialCountdownToSevenDays(SearchResultTrialGateFingerprint)

            // 6.196.0 additionally gates the trial page behind a server-driven flag
            // (rc3.a, defaulting to false when the server omits it): force it to true.
            // The premium gate above it is intentionally left untouched.
            SearchResultTrialGateFingerprint.method.apply {
                val flagReadIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.IGET_BOOLEAN &&
                        getReference<FieldReference>()?.let { it.definingClass == "Lrc3;" && it.name == "a" } == true
                }
                val flagRegister = getInstruction<OneRegisterInstruction>(flagReadIndex).registerA

                replaceInstruction(
                    flagReadIndex,
                    "const/4 v$flagRegister, 0x1"
                )
            }
        }
    }
}
