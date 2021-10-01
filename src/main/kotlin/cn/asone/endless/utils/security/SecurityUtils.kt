package cn.asone.endless.utils.security

import org.apache.commons.codec.digest.DigestUtils
import java.io.File

object SecurityUtils {
    @JvmStatic
    fun toHashValue(byteArray: ByteArray): String {
        val sb = StringBuilder()
        for (i in byteArray) {
            val hashHex = Integer.toHexString(i.toInt() and 0xFF)
            if (hashHex.length < 2)
                sb.append('0')
            sb.append(hashHex)
        }
        return sb.toString()
    }

    @JvmStatic
    fun getSha1(byteArray: ByteArray): ByteArray {
        val digest = DigestUtils.getSha1Digest()
        digest.update(byteArray)
        return digest.digest()
    }

    @JvmStatic
    fun getSha1(file: File): ByteArray {
        val digest = DigestUtils.getSha1Digest()
        digest.update(file.readBytes())
        return digest.digest()
    }

    @JvmStatic
    fun getMd5(file: File): ByteArray {
        val digest = DigestUtils.getMd5Digest()
        digest.update(file.readBytes())
        return digest.digest()
    }

    @JvmStatic
    fun getSha1String(file: File) = toHashValue(getSha1(file))

    @JvmStatic
    fun getMd5String(file: File) = toHashValue((getMd5(file)))
}