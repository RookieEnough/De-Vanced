/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/inbox/HideInboxAdsPatch.kt
 */
package app.morphe.patches.messenger.inbox

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch

@Suppress("unused")
val hideInboxAdsPatch = bytecodePatch(
    name = "Hide inbox ads",
    description = "Hides ads in inbox.",
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    execute {
        // Newer Messenger versions removed the native inbox-ads item supplier
        // entirely, so there is nothing to patch and nothing to hide. Match optionally so
        // selecting this patch never aborts the whole run on those versions; on older
        // versions that still have the ad loader it no-ops the load method as before.
        LoadInboxAdsFingerprint.methodOrNull?.replaceInstruction(0, "return-void")
    }
}
