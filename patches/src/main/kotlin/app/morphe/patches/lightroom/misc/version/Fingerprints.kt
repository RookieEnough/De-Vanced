/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/tree/main/patches/src/main/kotlin/app/revanced/patches/lightroom
 */
package app.morphe.patches.lightroom.misc.version

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodesFilter
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object RefreshRemoteConfigurationMethodFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    strings = listOf(
        "com.adobe.lrmobile.denylisted_version_set_key",
        "com.adobe.lrmobile.app_min_version_key",
    ),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET, // Overwrite this instruction to disable the check.
    ),
)
