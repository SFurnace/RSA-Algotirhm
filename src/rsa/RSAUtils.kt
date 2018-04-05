package rsa

import java.math.BigInteger

import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

private val ADDEND = 128

/**
 * 将一个 [ByteArray] 转换为一个全是非负数的 [IntArray]
 */
internal fun ByteArray.convertToPositiveInt(): IntArray = this.map { it.toInt() + ADDEND }.toIntArray()

/**
 * 将一个 [IntArray] 转换为 [ByteArray] 数组
 */
internal fun IntArray.convertToBytes(): ByteArray = this.map { (it - rsa.ADDEND).toByte() }.toByteArray()

/**
 * 实现了扩展欧几里得算法，用于找到以下方程中的一组(X, Y)。
 * aX + bY = gcd(a,b)
 *
 * @return 内容为[X, Y]的数组
 */
internal fun extEuclid(a: BigInteger, b: BigInteger): Array<BigInteger> {
    if (b > ZERO && a > b) {
        val r = a.gcd(b)
        return extEuclid(a, b, r)
    } else {
        throw IllegalArgumentException("a($a) must bigger than b($b).")
    }

}

private fun extEuclid(a: BigInteger, b: BigInteger, gcd: BigInteger): Array<BigInteger> {
    val k = a.divide(b)
    val c = a.mod(b)
    return if (c == gcd) {
        arrayOf(ONE, k.negate())
    } else {
        val nextStep = extEuclid(b, c, gcd)
        arrayOf(nextStep[1], nextStep[0].subtract(nextStep[1].multiply(k)))
    }
}

