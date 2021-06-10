import keystone.*
import kotlinx.cinterop.*

fun main(){
    val asm = "INC ecx; DEC edx"
    memScoped {
        var data:ByteArray = byteArrayOf()
        val ks:COpaquePointerVar = alloc()
        val count:COpaquePointerVar = alloc()
        val encode:COpaquePointerVar = alloc()
        val size:COpaquePointerVar = alloc()

        println("Is ARM64 supported: ${ks_arch_supported(KS_ARCH_ARM64)}")
        println("Is x86 supported: ${ks_arch_supported(KS_ARCH_X86)}")

        val err:ks_err = ks_open(KS_ARCH_X86, KS_MODE_32.toInt() + KS_MODE_LITTLE_ENDIAN.toInt(), ks.ptr.reinterpret())
        if(err != KS_ERR_OK){
            println("ERROR: failed on ks_open(), quit")
            return
        }
        val ksAsm = ks_asm(ks.value?.reinterpret(), asm, 0u, encode.ptr.reinterpret(), size.ptr.reinterpret(), count.ptr.reinterpret())
        if(ksAsm != KS_ERR_OK.toInt()){
            println("ERROR: ks_asm() failed & count = ${count.value.toLong()}, error = ${ks_errno(ks.value?.reinterpret())}");
        }else{
            val paddedSize = if(size.value.toLong() > 0) size.value.toLong().toInt() else 0
            if(paddedSize == 0){
                return
            }
            data = encode.ptr.readBytes(paddedSize).copyOf()
            println("Size of data: ${data.size} size: $paddedSize")
            println("$asm = ${data.toHexString()}")
            println("Compiled: ${size.value.toLong()} bytes, statements: ${count.value.toLong()}")
        }
        println("We all done")
        ks_free(encode.value?.reinterpret())
        ks_close(ks.value?.reinterpret())
        return
    }
}