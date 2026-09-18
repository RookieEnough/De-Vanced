/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/cricbuzz/misc/extension/SharedExtensionPatch.kt
 */
package app.morphe.patches.cricbuzz.misc.extension

import app.morphe.patches.shared.misc.extension.ExtensionHook
import app.morphe.patches.shared.misc.extension.sharedExtensionPatch
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private class ApplicationInitHook : ExtensionHook(
    fingerprint = ApplicationInitFingerprint,
    insertIndexResolver = { method: Method ->
        val getApplicationContextIndex = method.indexOfFirstInstructionOrThrow {
            getReference<MethodReference>()?.name == "getApplicationContext"
        }

        // Below the move-result-object instruction.
        getApplicationContextIndex + 2
    },
    contextRegisterResolver = { method: Method ->
        val getApplicationContextIndex = method.indexOfFirstInstructionOrThrow {
            getReference<MethodReference>()?.name == "getApplicationContext"
        }

        val moveResultInstruction =
            method.implementation!!.instructions.elementAt(getApplicationContextIndex + 1) as OneRegisterInstruction

        "v${moveResultInstruction.registerA}"
    },
)

internal val applicationInitHook: ExtensionHook = ApplicationInitHook()

val sharedExtensionPatch = sharedExtensionPatch(
    extensionName = "cricbuzz",
    isGmsCorePatch = false,
    applicationInitHook,
)

