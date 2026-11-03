/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/inbox/HideInboxSubtabsPatch.kt
 */
package app.morphe.patches.messenger.inbox

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val hideInboxSubtabsPatch = bytecodePatch(
    name = "Hide inbox subtabs",
    description = "Hides Home and Channels tabs between active now tray and chats.",
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    execute {
        CreateInboxSubTabsFingerprint.let {
            // Flip the "subtabs loaded" flag value from true to false.
            val index = it.instructionMatches.first().index
            val register = it.method.getInstruction<OneRegisterInstruction>(index).registerA
            it.method.replaceInstruction(index, "const/4 v$register, 0x0")
        }
    }
}
