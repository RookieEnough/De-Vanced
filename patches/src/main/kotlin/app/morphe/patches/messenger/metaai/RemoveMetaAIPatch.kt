/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/metaai/RemoveMetaAIPatch.kt
 */
package app.morphe.patches.messenger.metaai

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.messenger.misc.extension.sharedExtensionPatch
import app.morphe.util.findMutableMethodOf
import app.morphe.util.getReference
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

internal const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/messenger/metaai/RemoveMetaAIPatch;"
internal const val EXTENSION_METHOD_NAME = "overrideBooleanFlag"

/**
 * Meta AI navigation-drawer/menu items that the mobile-config override above does not cover.
 * Each is shown by a no-argument boolean "lazy loader" gate that references the plugin's
 * class name and returns whether to build the item; forcing those gates to `false` hides them.
 */
private val META_AI_MENU_PLUGIN_IDS = setOf(
    // "Create an AI" item in the menu.
    "com.facebook.messaging.navigation.plugins.aicreationfolder.folderitem.AiCreationFolderItem",
    // "Chat with AIs" (AI home) item in the menu.
    "com.facebook.messaging.navigation.plugins.aihomefolder.folderitem.AiHomeFolderItem",
)

@Suppress("unused")
val removeMetaAIPatch = bytecodePatch(
    name = "Remove Meta AI",
    description = "Removes UI elements related to Meta AI."
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    dependsOn(sharedExtensionPatch)

    execute {
        GetMobileConfigBoolFingerprint.method.apply {
            val returnIndex = GetMobileConfigBoolFingerprint.instructionMatches.first().index
            val returnRegister = getInstruction<OneRegisterInstruction>(returnIndex).registerA

            addInstructions(
                returnIndex,
                """
                    invoke-static { p1, p2, v$returnRegister }, $EXTENSION_CLASS_DESCRIPTOR->$EXTENSION_METHOD_NAME(JZ)Z
                    move-result v$returnRegister
                """
            )
        }

        // Extract the common starting digits of Meta AI flag IDs from a flag found in code.
        val relevantDigits = with(MetaAIKillSwitchCheckFingerprint) {
            method.getInstruction<WideLiteralInstruction>(instructionMatches.first().index).wideLiteral
        }.toString().substring(0, 7)

        // Replace placeholder in the extension method.
        with(ExtensionMethodFingerprint) {
            method.replaceInstruction(
                stringMatches.first().index,
                """
                    const-string v1, "$relevantDigits"
                """
            )
        }

        // Remove the Meta AI floating button ("AI FAB") from every surface it appears on
        // (Chats, Notifications, etc.) by forcing its component's render to return null.
        AiFabComponentFingerprint.method.returnEarly(null)

        // Hide the "Create an AI" and "Chat with AIs" menu items by forcing their
        // visibility gates to report the item as disabled. There is one gate per surface,
        // so every no-arg boolean method that references the plugin id is neutralized.
        classDefForEach { classDef ->
            classDef.methods.forEach forEachMethod@{ method ->
                if (method.parameterTypes.isNotEmpty() || method.returnType != "Z") return@forEachMethod

                val gatesMenuItem = method.implementation?.instructions?.any { instruction ->
                    (instruction.opcode == Opcode.CONST_STRING || instruction.opcode == Opcode.CONST_STRING_JUMBO) &&
                        instruction.getReference<StringReference>()?.string in META_AI_MENU_PLUGIN_IDS
                } ?: false

                if (gatesMenuItem) {
                    mutableClassDefBy(classDef).findMutableMethodOf(method).returnEarly(false)
                }
            }
        }
    }
}
