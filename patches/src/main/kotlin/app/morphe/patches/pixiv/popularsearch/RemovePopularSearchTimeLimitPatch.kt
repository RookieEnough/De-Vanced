/*
 * Copyright 2025 De-Vanced.
 * https://github.com/RookieEnough/De-Vanced
 */

package app.morphe.patches.pixiv.popularsearch

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.compat.AppCompatibilities
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c

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
            // that defaults to false when the server omits it: force the flag read
            // to true. The read is located structurally (an iget-boolean directly
            // feeding an if-eqz on the same register, unique in this method), so no
            // obfuscated names are involved. The premium gate above it is
            // intentionally left untouched.
            SearchResultTrialGateFingerprint.method.apply {
                val instructions = implementation?.instructions?.toList()
                    ?: throw PatchException("Trial gate method has no implementation")

                var flagReadIndex = -1
                for (i in 1 until instructions.size) {
                    val previous = instructions[i - 1]
                    val current = instructions[i]
                    val previousRegister = (previous as? Instruction22c)
                        ?.takeIf { previous.opcode == Opcode.IGET_BOOLEAN }?.registerA
                    val currentRegister = (current as? Instruction21t)
                        ?.takeIf { current.opcode == Opcode.IF_EQZ }?.registerA

                    if (previousRegister != null && previousRegister == currentRegister) {
                        if (flagReadIndex != -1) throw PatchException("Ambiguous trial flag read")
                        flagReadIndex = i - 1
                    }
                }
                if (flagReadIndex == -1) throw PatchException("Trial flag read not found")

                val flagRegister = (instructions[flagReadIndex] as Instruction22c).registerA
                replaceInstruction(
                    flagReadIndex,
                    "const/4 v$flagRegister, 0x1"
                )
            }
        }
    }
}
