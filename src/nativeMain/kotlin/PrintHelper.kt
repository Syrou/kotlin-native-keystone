import kotlin.experimental.and

fun ByteArray.toHexString() = joinToString(" ") { (0xFF and it.toInt()).toString(16).padStart(2, '0') }