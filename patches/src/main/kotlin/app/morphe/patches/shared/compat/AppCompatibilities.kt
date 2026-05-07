/*
 * Forked from:
 * https://gitlab.com/ReVanced/revanced-patches/-/blob/main/patches/src/main/kotlin/app/revanced/patches/shared/compat/AppCompatibilities.kt
 *
 * Central Morphe `Compatibility` metadata so Morphe Manager shows human-readable app names.
 */
package app.morphe.patches.shared.compat

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

@Suppress("MemberVisibilityCanBePrivate")
internal object AppCompatibilities {
    private const val TIKTOK_COLOR = 0xFE2C55

    val AMAZON_MUSIC = Compatibility(
        name = "Amazon Music",
        packageName = "com.amazon.mp3",
        appIconColor = 0xFF9900,
    )

    val AMAZON_SHOPPING = Compatibility(
        name = "Amazon Shopping",
        packageName = "com.amazon.mShop.android.shopping",
        appIconColor = 0xFF9900,
    )

    val ANGULUS = Compatibility(
        name = "Angulus",
        packageName = "com.drinkplusplus.angulus",
        appIconColor = 0x2196F3,
    )

    val BANDCAMP = Compatibility(
        name = "Bandcamp",
        packageName = "com.bandcamp.android",
        appIconColor = 0x629AA9,
    )

    val GMX_MAIL = Compatibility(
        name = "GMX Mail",
        packageName = "de.gmx.mobile.android.mail",
        appIconColor = 0x003399,
    )

    val GOOGLE_RECORDER = Compatibility(
        name = "Google Recorder",
        packageName = "com.google.android.apps.recorder",
        appIconColor = 0x4285F4,
    )

    val HEX_EDITOR = Compatibility(
        name = "Hex Editor",
        packageName = "com.myprog.hexedit",
        appIconColor = 0x607D8B,
    )

    val IRPLUS = Compatibility(
        name = "irplus",
        packageName = "net.binarymode.android.irplus",
        appIconColor = 0xFF5722,
    )

    val NOTHING_X = Compatibility(
        name = "Nothing X",
        packageName = "com.nothing.smartcenter",
        appIconColor = 0x000000,
    )

    val PEACOCK_TV = Compatibility(
        name = "Peacock TV",
        packageName = "com.peacocktv.peacockandroid",
        appIconColor = 0x000000,
    )

    val NU_NL = Compatibility(
        name = "NU.nl",
        packageName = "nl.sanomamedia.android.nu",
        appIconColor = 0xE2001A,
    )

    val PHOTOSHOP_MIX = Compatibility(
        name = "Photoshop Mix",
        packageName = "com.adobe.photoshopmix",
        appIconColor = 0x31A8FF,
    )

    val FACEBOOK = Compatibility(
        name = "Facebook",
        packageName = "com.facebook.katana",
        appIconColor = 0x0866FF,
    )

    val FACEBOOK_490 = Compatibility(
        name = "Facebook",
        packageName = "com.facebook.katana",
        appIconColor = 0x0866FF,
        targets = listOf(AppTarget("490.0.0.63.82")),
    )

    val MESSENGER = Compatibility(
        name = "Messenger",
        packageName = "com.facebook.orca",
        appIconColor = 0x0866FF,
    )

    val THREADS = Compatibility(
        name = "Threads",
        packageName = "com.instagram.barcelona",
        appIconColor = 0x000000,
        targets = listOf(AppTarget("382.0.0.51.85")),
    )

    val GOOGLE_PHOTOS = Compatibility(
        name = "Google Photos",
        packageName = "com.google.android.apps.photos",
        appIconColor = 0xFC3F3C,
    )

    val GOOGLE_NEWS = Compatibility(
        name = "Google News",
        packageName = "com.google.android.apps.magazines",
        appIconColor = 0x4887F4,
        targets = listOf(AppTarget("5.108.0.644447823")),
    )

    val INSHORTS = Compatibility(
        name = "Inshorts",
        packageName = "com.nis.app",
        appIconColor = 0xE53935,
    )

    val LETTERBOXD = Compatibility(
        name = "Letterboxd",
        packageName = "com.letterboxd.letterboxd",
        appIconColor = 0x00B020,
    )

    val LIGHTROOM = Compatibility(
        name = "Lightroom",
        packageName = "com.adobe.lrmobile",
        appIconColor = 0x31A8FF,
        targets = listOf(AppTarget("9.3.0")),
    )

    val PIXIV = Compatibility(
        name = "Pixiv",
        packageName = "jp.pxv.android",
        appIconColor = 0x0096FA,
        targets = listOf(AppTarget("6.141.1")),
    )

    val RAR = Compatibility(
        name = "RAR",
        packageName = "com.rarlab.rar",
        appIconColor = 0xFF9800,
    )

    val CRICBUZZ = Compatibility(
        name = "Cricbuzz",
        packageName = "com.cricbuzz.android",
        appIconColor = 0x009270,
        targets = listOf(AppTarget("6.24.01")),
    )

    val MICROSOFT_LENS = Compatibility(
        name = "Microsoft Lens",
        packageName = "com.microsoft.office.officelens",
        appIconColor = 0xD73E01,
    )

    val DISNEY_PLUS = Compatibility(
        name = "Disney+",
        packageName = "com.disney.disneyplus",
        appIconColor = 0x09A9B8,
    )

    val PHOTOMATH = Compatibility(
        name = "Photomath",
        packageName = "com.microblink.photomath",
        appIconColor = 0xD23F3F,
    )

    val PROTON_MAIL = Compatibility(
        name = "Proton Mail",
        packageName = "ch.protonmail.android",
        appIconColor = 0x6D48FF,
        targets = listOf(AppTarget("4.15.0")),
    )

    val ICON_PACK_STUDIO = Compatibility(
        name = "Icon Pack Studio",
        packageName = "ginlemon.iconpackstudio",
        appIconColor = 0xF4287D,
        targets = listOf(AppTarget("2.2 build 016")),
    )

    val SOUNDCLOUD = Compatibility(
        name = "SoundCloud",
        packageName = "com.soundcloud.android",
        appIconColor = 0x000000,
        targets = listOf(AppTarget("2025.05.27-release")),
    )

    val STRAVA = Compatibility(
        name = "Strava",
        packageName = "com.strava",
        appIconColor = 0xFC6925,
    )

    val TUMBLR = Compatibility(
        name = "Tumblr",
        packageName = "com.tumblr",
        appIconColor = 0x001834,
    )

    val TWITCH = Compatibility(
        name = "Twitch",
        packageName = "tv.twitch.android.app",
        appIconColor = 0x9146FF,
        targets = listOf(AppTarget("16.9.1"), AppTarget("25.3.0")),
    )

    val VIBER = Compatibility(
        name = "Viber",
        packageName = "com.viber.voip",
        appIconColor = 0x7360F2,
    )

    val VIBER_VERSIONED = Compatibility(
        name = "Viber",
        packageName = "com.viber.voip",
        appIconColor = 0x7360F2,
        targets = listOf(AppTarget("25.9.2.0"), AppTarget("26.1.2.0")),
    )

    /** TikTok JP + global, versions 43.6.2 and 43.8.3 (feed filter). */
    fun tiktok4362And4383(): Array<Compatibility> = arrayOf(
        Compatibility(
            name = "TikTok (JP)",
            packageName = "com.ss.android.ugc.trill",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.6.2"), AppTarget("43.8.3")),
        ),
        Compatibility(
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.6.2"), AppTarget("43.8.3")),
        ),
    )

    /** TikTok JP + global, version 43.8.3 only. */
    fun tiktok4383(): Array<Compatibility> = arrayOf(
        Compatibility(
            name = "TikTok (JP)",
            packageName = "com.ss.android.ugc.trill",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.8.3")),
        ),
        Compatibility(
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.8.3")),
        ),
    )

    /** TikTok JP + global, version 43.6.2 only. */
    fun tiktok4362(): Array<Compatibility> = arrayOf(
        Compatibility(
            name = "TikTok (JP)",
            packageName = "com.ss.android.ugc.trill",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.6.2")),
        ),
        Compatibility(
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            appIconColor = TIKTOK_COLOR,
            targets = listOf(AppTarget("43.6.2")),
        ),
    )

    /** TikTok JP + global, any version. */
    fun tiktokAny(): Array<Compatibility> = arrayOf(
        Compatibility(
            name = "TikTok (JP)",
            packageName = "com.ss.android.ugc.trill",
            appIconColor = TIKTOK_COLOR,
        ),
        Compatibility(
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            appIconColor = TIKTOK_COLOR,
        ),
    )
}
