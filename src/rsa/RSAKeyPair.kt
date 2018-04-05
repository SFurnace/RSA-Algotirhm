package rsa

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import java.security.SecureRandom

class RSAKeyPair(val privateKey: RSAKey, val publicKey: RSAKey) {
    companion object {
        private const val n0 = 128
        private const val n1 = 64

        fun generateKeyPair(): RSAKeyPair {
            val random = SecureRandom()
            val p = BigInteger.probablePrime(n0, random)
            val q = BigInteger.probablePrime(n0, random)

            val n = p.multiply(q)
            val r = p.subtract(ONE).multiply(q.subtract(ONE))
            val e = run {
                var b = BigInteger.probablePrime(n1, random)
                while (true) {
                    if (r.gcd(b) == ONE) {
                        break
                    } else {
                        b = BigInteger.probablePrime(n1, random)
                    }
                }
                return@run b
            }
            val d = extEuclid(r, e)[1].let {
                when {
                    (it > ZERO) -> it
                    else -> r.add(it)
                }
            }

            return RSAKeyPair(RSAKey(n, e), RSAKey(n, d))
        }
    }
}
