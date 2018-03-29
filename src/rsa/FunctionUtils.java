package rsa;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

class FunctionUtils {
    private static final int ADDEND = 128;

    static int[] convertBytesToIntegers(byte[] bytes) {
        int[] integers = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            integers[i] = bytes[i] + ADDEND;
        }
        return integers;
    }

    static byte[] convertIntegersToBytes(int[] integers) {
        byte[] bytes = new byte[integers.length];
        for (int i = 0; i < integers.length; i++) {
            bytes[i] = (byte) (integers[i] - ADDEND);
        }
        return bytes;
    }

    static BigInteger[] extEuclid(BigInteger a, BigInteger b) {
        if (b.compareTo(ZERO) > 0 && a.compareTo(b) > 0) {
            BigInteger r = a.gcd(b);
            return extEuclid(a, b, r);
        } else {
            throw new IllegalArgumentException();
        }

    }

    private static BigInteger[] extEuclid(BigInteger a, BigInteger b, BigInteger gcd) {
        BigInteger k = a.divide(b), c = a.mod(b);
        if (c.equals(gcd)) {
            return new BigInteger[]{ONE, k.negate()};
        } else {
            BigInteger[] nextStep = extEuclid(b, c, gcd);
            return new BigInteger[]{nextStep[1], nextStep[0].subtract(nextStep[1].multiply(k))};
        }
    }
}
