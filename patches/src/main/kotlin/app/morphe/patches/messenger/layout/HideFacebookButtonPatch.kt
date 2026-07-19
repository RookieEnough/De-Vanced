/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/layout/HideFacebookButtonPatch.kt
 */
package app.morphe.patches.messenger.layout

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.findMutableMethodOf
import app.morphe.util.getReference
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

/**
 * Every Facebook entry point in Messenger is a UI plugin. Whether it is shown is decided
 * by a no-argument boolean "lazy loader" method that constructs the plugin instance
 * (`new-instance`) when the element should appear and returns `true`, or returns `false`
 * to hide it. There is one such gate per surface the element can appear on
 * (e.g. the toolbar button has separate gates for the inbox and the Marketplace folder),
 * so all of them must be neutralized to remove the element everywhere.
 *
 * Targeting the plugin classes by their (stable) descriptors instead of the individual,
 * obfuscated loader methods keeps this robust across app updates.
 */
private val FACEBOOK_ENTRY_POINT_CLASSES = setOf(
    // Facebook button in the inbox top toolbar.
    "Lcom/facebook/messaging/inbox/tab/plugins/core/tabtoolbarbutton/facebookbutton/facebooktoolbarbutton/FacebookButtonTabButtonImplementation;",
    // Facebook icon in the Marketplace folder navigation bar (opens fb://marketplace).
    "Lcom/facebook/messaging/marketplace/plugins/folder/navbarmenuitem/NavBarMenuItemImplementation;",
    // "View Facebook profile" action button in a chat's settings.
    "Lcom/facebook/messaging/profile/plugins/core/threadsettingsactionbutton/facebookprofile/ThreadSettingsFacebookProfileActionButton;",
    // "Also from Meta" shortcuts section in the menu (Facebook Reels, Events, etc.).
    "Lcom/facebook/messaging/navigation/plugins/drawerfoldersections/fbshortcutsfoldersection/FacebookShortcutsFolderSection;",
    // "Share to Facebook" button on a community channel invite.
    "Lcom/facebook/messaging/communitymessaging/plugins/channelinvite/sharetofacebookbutton/ShareToFacebookButtonImplementation;",
    // "Share to Facebook" button in the public chats external share row.
    "Lcom/facebook/messaging/publicchats/plugins/externalsharehscrollbuttons/sharetofacebook/ShareToFacebookHScrollButtonImplementation;",
)

@Suppress("unused")
val hideFacebookButtonPatch = bytecodePatch(
    name = "Hide Facebook buttons",
    description = "Hides buttons and shortcuts that open Facebook.",
) {
    compatibleWith(AppCompatibilities.MESSENGER)

    execute {
        classDefForEach { classDef ->
            classDef.methods.forEach forEachMethod@{ method ->
                if (method.parameterTypes.isNotEmpty() || method.returnType != "Z") return@forEachMethod

                val constructsEntryPoint = method.implementation?.instructions?.any { instruction ->
                    instruction.opcode == Opcode.NEW_INSTANCE &&
                        instruction.getReference<TypeReference>()?.type in FACEBOOK_ENTRY_POINT_CLASSES
                } ?: false

                if (constructsEntryPoint) {
                    mutableClassDefBy(classDef).findMutableMethodOf(method).returnEarly(false)
                }
            }
        }
    }
}
