package rsa

import java.io.Serializable

class RSAEncryptedFile(val fileName: String, val encryptedContent: ByteArray) : Serializable
