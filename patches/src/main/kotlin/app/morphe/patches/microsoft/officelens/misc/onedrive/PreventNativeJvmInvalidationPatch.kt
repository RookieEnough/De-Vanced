package app.morphe.patches.microsoft.officelens.misc.onedrive

import app.morphe.patches.shared.compat.AppCompatibilities
import app.morphe.patcher.patch.resourcePatch
import java.io.RandomAccessFile

private const val PLAT_LIBRARY_PATH = "lib/arm64-v8a/libplat.so"

private fun bytesOf(vararg values: Int) =
    ByteArray(values.size) { index -> values[index].toByte() }

private val JVM_INVALIDATION_PATTERN = bytesOf(
    0xA8, 0x03, 0x00, 0xF0,
    0xE0, 0x03, 0x1F, 0x2A,
    0x08, 0xFD, 0x44, 0xF9,
    0x1F, 0x01, 0x00, 0xF9,
)
private val NULL_STORE = bytesOf(0x1F, 0x01, 0x00, 0xF9)
private val NOP = bytesOf(0x1F, 0x20, 0x03, 0xD5)

private fun ByteArray.indexesOf(pattern: ByteArray): List<Int> = buildList {
    val content = this@indexesOf
    for (offset in 0..content.size - pattern.size) {
        if (content.copyOfRange(offset, offset + pattern.size).contentEquals(pattern)) {
            add(offset)
        }
    }
}

/**
 * Lens checks the APK signing certificate in libplat. After the APK is re-signed,
 * a delayed worker writes null to the JavaVM pointer, causing later native
 * telemetry and timer callbacks to dereference null. Neutralize only that store.
 */
@Suppress("unused")
val preventNativeJvmInvalidationPatch = resourcePatch(
    name = "Prevent native JVM invalidation",
    description = "Prevents Microsoft Lens's native signature check from clearing the " +
        "JavaVM pointer and causing delayed crashes after the APK is re-signed.",
) {
    compatibleWith(AppCompatibilities.MICROSOFT_LENS)

    execute {
        RandomAccessFile(get(PLAT_LIBRARY_PATH), "rw").use { libplat ->
            val contents = ByteArray(libplat.length().toInt())
            libplat.seek(0)
            libplat.readFully(contents)

            val patternOffset = contents.indexesOf(JVM_INVALIDATION_PATTERN).singleOrNull()
                ?: error("Expected one native JVM invalidation pattern in libplat")
            val nullStoreOffset = patternOffset + JVM_INVALIDATION_PATTERN.size - NULL_STORE.size
            check(
                contents.copyOfRange(
                    nullStoreOffset,
                    nullStoreOffset + NULL_STORE.size,
                ).contentEquals(NULL_STORE),
            ) {
                "Unexpected libplat JVM invalidation instruction"
            }

            libplat.seek(nullStoreOffset.toLong())
            libplat.write(NOP)
        }
    }
}
