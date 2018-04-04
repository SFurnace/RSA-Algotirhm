package rsa;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class RSATest {
    @Test
    void test0() throws IOException, ClassNotFoundException {
        RSAKeyPair rsaKeys = RSAKeyPair.generateKeyPair();
        RSA rsa0 = new RSA(rsaKeys.getPrivateKey()), rsa1 = new RSA(rsaKeys.getPublicKey());
        String str = "Hello World!fjdlksajfldsja;fldjslajfdl;sjalfdjs积分到龙A级分量空甲A啃老反动拉肯江凯撒1";
        byte[] encrypted = rsa0.encryptObj(str);
        String decrypted = rsa1.decryptObj(encrypted);

        System.out.println(str);
        System.out.println(Arrays.toString(encrypted));
        System.out.println(decrypted);
    }
}