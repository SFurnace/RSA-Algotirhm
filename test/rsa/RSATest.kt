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
        val str = "Hello World!fjdlksajfldsja;fldjslajfdl;sjalfdjs积分到龙A级分量空甲A啃老反动拉肯江凯撒1"
        val encrypted = rsa0.encryptObj(str)
        val decrypted = rsa1.decryptObj<String>(encrypted)

        println(str)
        println(encrypted.contentToString())
        println(decrypted)
    }
}