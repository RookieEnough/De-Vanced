/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/tree/main/patches/src/main/kotlin/app/revanced/patches/lightroom
 */
package app.morphe.patches.lightroom.misc.premium

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodesFilter
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object HasPurchasedMethodFingerprint : Fingerprint(
    definingClass = "Ll7/a;",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "Z",
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.SGET_OBJECT,
        Opcode.CONST_4,
        Opcode.CONST_4,
        Opcode.CONST_4,
    ),
)
