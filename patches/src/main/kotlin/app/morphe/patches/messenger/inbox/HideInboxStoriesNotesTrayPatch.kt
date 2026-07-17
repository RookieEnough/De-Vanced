package app.morphe.patches.messenger.inbox

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

@Suppress("unused")
val hideInboxStoriesNotesTrayPatch = bytecodePatch(
    name = "Hide inbox stories and notes tray",
    description = "Hides the stories and notes horizontal tray at the top of the inbox.",
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    execute {
        FriendsInboxTrayFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """.trimIndent()
        )
    }
}
