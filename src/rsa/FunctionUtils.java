package rsa;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
 * 本类中包含一些辅助方法
 */
class FunctionUtils {
    private static final int ADDEND = 128;

    /**
     * 将一个byte数组转换为一个全是非负数的int数组
     */
    static int[] convertBytesToIntegers(byte[] bytes) {
        int[] integers = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            integers[i] = bytes[i] + ADDEND;
        }
        return integers;
    }

    /**
     * 将一个int数组转换为byte数组
     */
    static byte[] convertIntegersToBytes(int[] integers) {
        byte[] bytes = new byte[integers.length];
        for (int i = 0; i < integers.length; i++) {
            bytes[i] = (byte) (integers[i] - ADDEND);
        }
        return bytes;
    }

    /**
     * 实现了扩展欧几里得算法，用于找到以下方程中的一组(X, Y)。
     * aX + bY = gcd(a,b)
     *
     * @return 内容为[X, Y]的数组
     */
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
