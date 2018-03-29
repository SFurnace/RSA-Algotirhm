package rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static rsa.FunctionUtils.extEuclid;

public class KeyPair {
    private static final int n0 = 128;
    private static final int n1 = 64;

    private RSAKey privateKey;
    private RSAKey publicKey;

    private KeyPair(RSAKey privateKey, RSAKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static KeyPair generateKeyPair() {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(n0, random);
        BigInteger q = BigInteger.probablePrime(n0, random);

        BigInteger n = p.multiply(q);
        BigInteger r = p.subtract(ONE).multiply(q.subtract(ONE));
        BigInteger e, d;

        while (true) {
            BigInteger b = BigInteger.probablePrime(n1, random);
            if (r.gcd(b).equals(ONE)) {
                e = b;
                break;
            }
        }
        d = extEuclid(r, e)[1];
        if (d.compareTo(ZERO) < 0) {
            d = r.add(d);
        }

        return new KeyPair(new RSAKey(n, e), new RSAKey(n, d));
    }

    public RSAKey getPrivateKey() {
        return privateKey;
    }

    public RSAKey getPublicKey() {
        return publicKey;
    }
}
