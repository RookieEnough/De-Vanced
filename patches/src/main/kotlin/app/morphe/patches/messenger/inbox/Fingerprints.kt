/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/messenger/inbox/Fingerprints.kt
 */
package app.morphe.patches.messenger.inbox

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodesFilter
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue

internal object CreateInboxSubTabsFingerprint : Fingerprint(
    returnType = "V",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID,
    ),
    custom = { method, classDef ->
        method.name == "run" &&
            classDef.fields.any { field ->
                if (field.name != "__redex_internal_original_name") return@any false
                (field.initialValue as? StringEncodedValue)?.value == "InboxSubtabsItemSupplierImplementation\$onSubscribe\$1"
            }
    },
)

internal object LoadInboxAdsFingerprint : Fingerprint(
    returnType = "V",
    strings = listOf(
        "ads_load_begin",
        "inbox_ads_fetch_start",
    ),
    custom = { method, _ ->
        method.definingClass == "Lcom/facebook/messaging/business/inboxads/plugins/inboxads/itemsupplier/" +
            "InboxAdsItemSupplierImplementation;"
    },
)

internal object FriendsInboxTrayFingerprint : Fingerprint(
    returnType = "Z",
    strings = listOf("com.facebook.messaging.friendsinboxunit.plugins.inboxunit.FriendsInboxUnitKillSwitch"),
)
