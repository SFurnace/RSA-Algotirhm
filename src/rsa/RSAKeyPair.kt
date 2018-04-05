package rsa

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import java.security.SecureRandom

class RSAKeyPair private constructor(val privateKey: RSAKey, val publicKey: RSAKey) {
    companion object {
        private val n0 = 128
        private val n1 = 64

        fun generateKeyPair(): RSAKeyPair {
            val random = SecureRandom()
            val p = BigInteger.probablePrime(n0, random)
            val q = BigInteger.probablePrime(n0, random)

            val n = p.multiply(q)
            val r = p.subtract(ONE).multiply(q.subtract(ONE))
            val e: BigInteger
            var d: BigInteger

            while (true) {
                val b = BigInteger.probablePrime(n1, random)
                if (r.gcd(b) == ONE) {
                    e = b
                    break
                }
            }
            d = extEuclid(r, e)[1]
            if (d.compareTo(ZERO) < 0) {
                d = r.add(d)
            }

            return RSAKeyPair(RSAKey(n, e), RSAKey(n, d))
        }
    }
}
