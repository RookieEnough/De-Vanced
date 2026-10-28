/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/viber/ads/HideAdsPatch.kt
 */
package app.morphe.patches.viber.ads

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide Ads",
    description = "Hides ad banners between chats.",
) {
    compatibleWith(AppCompatibilities.VIBER)

    execute {
        val methodMatch = FindAdStringMethodFingerprint
        val referenceIndex =
            methodMatch.method.indexOfFirstInstructionOrThrow(Opcode.NEW_INSTANCE)

        val targetType =
            methodMatch.method.getInstruction<ReferenceInstruction>(referenceIndex)
                .getReference<TypeReference>()
                ?.type
                ?: return@execute

        val targetClass = classDefBy(targetType)
        val adFreeMethod = mutableClassDefBy(targetClass).methods.firstOrNull { m ->
            m.parameterTypes.isEmpty() && m.returnType == "I"
        } ?: return@execute

        adFreeMethod.returnEarly(1)
    }
}

