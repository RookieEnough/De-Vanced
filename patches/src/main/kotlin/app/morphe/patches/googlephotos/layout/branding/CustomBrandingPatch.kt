package app.morphe.patches.googlephotos.layout.branding

import app.morphe.patcher.patch.resourcePatch
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources
import app.morphe.util.forEachChildElement
import org.w3c.dom.Element

private const val ICON_RESOURCE_NAME = "morphe_photos_launcher"
private const val APP_NAME = "ReVanced Google Photos"

private val mipmapDirectories = listOf(
    "mipmap-mdpi",
    "mipmap-hdpi",
    "mipmap-xhdpi",
    "mipmap-xxhdpi",
    "mipmap-xxxhdpi",
)

/**
 * Returns true if this element contains a child <intent-filter>
 * with the android.intent.category.LAUNCHER category.
 */
private fun Element.hasLauncherIntent(): Boolean {
    forEachChildElement { child ->
        if (child.tagName == "intent-filter") {
            child.forEachChildElement { inner ->
                if (inner.tagName == "category" &&
                    inner.getAttribute("android:name") == "android.intent.category.LAUNCHER"
                ) return true
            }
        }
    }
    return false
}

private fun Element.applyBranding() {
    setAttribute("android:label", APP_NAME)
    setAttribute("android:icon", "@mipmap/$ICON_RESOURCE_NAME")
    if (hasAttribute("android:roundIcon")) {
        setAttribute("android:roundIcon", "@mipmap/$ICON_RESOURCE_NAME")
    }
}

@Suppress("unused")
val customBrandingPatch = resourcePatch(
    name = "Custom branding",
    description = "Changes the app name to \"$APP_NAME\" and replaces the app icon.",
) {
    execute {
        // 1. Read the original launcher icon resource name from the manifest,
        //    then patch all manifest entries that affect what the launcher shows.
        var originalIconName = ""
        document("AndroidManifest.xml").use { document ->
            val application = document.getElementsByTagName("application").item(0) as Element

            // Capture original icon name (e.g. "ic_launcher_photos") before overwriting.
            originalIconName = application
                .getAttribute("android:icon")
                .removePrefix("@mipmap/")
                .removePrefix("@drawable/")

            // Patch <application> tag.
            application.applyBranding()

            // Patch any <activity-alias> or <activity> that owns the LAUNCHER intent,
            // because those override the application-level label and icon in the launcher.
            for (tag in listOf("activity-alias", "activity")) {
                val nodes = document.getElementsByTagName(tag)
                for (i in 0 until nodes.length) {
                    val element = nodes.item(i) as Element
                    if (element.hasLauncherIntent()) element.applyBranding()
                }
            }
        }

        // 2. Copy our bundled PNG into the APK as a new named resource.
        mipmapDirectories.forEach { dpi ->
            copyResources(
                "googlephotos-branding",
                ResourceGroup(dpi, "$ICON_RESOURCE_NAME.png"),
            )
        }

        // 3. Overwrite the original launcher icon files so the splash screen
        //    (which still references the original resource name internally) shows our icon.
        if (originalIconName.isNotEmpty() && originalIconName != ICON_RESOURCE_NAME) {
            val resDir = get("res")
            mipmapDirectories.forEach { dpi ->
                val src = resDir.resolve("$dpi/$ICON_RESOURCE_NAME.png")
                val dst = resDir.resolve("$dpi/$originalIconName.png")
                if (src.exists() && dst.exists()) src.copyTo(dst, overwrite = true)
            }
        }
    }
}
