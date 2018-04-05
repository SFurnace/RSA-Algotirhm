package rsa

import java.io.Serializable
import java.util.*

data class RSAEncryptedFile(val fileName: String, val encryptedContent: ByteArray) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RSAEncryptedFile

        if (fileName != other.fileName) return false
        if (!Arrays.equals(encryptedContent, other.encryptedContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + Arrays.hashCode(encryptedContent)
        return result
    }
}
