package rsa

import org.junit.jupiter.api.Test
import java.io.IOException

class RSATest {
    @Test
    @Throws(IOException::class, ClassNotFoundException::class)
    fun test0() {
        val rsaKeys = RSAKeyPair.generateKeyPair()
        val rsa0 = RSA(rsaKeys.privateKey)
        val rsa1 = RSA(rsaKeys.publicKey)
        val str = "Hello World! 将代码中的CSS部分写到css文件里面。"
        val encrypted = rsa0.encryptObj(str)
        val decrypted = rsa1.decryptObj(encrypted) as String

        println(str)
        println(encrypted.contentToString())
        println(decrypted)
    }
}