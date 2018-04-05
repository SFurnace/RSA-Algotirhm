package rsa

import java.io.Serializable
import java.math.BigInteger
import java.math.BigInteger.*

class RSAKey(val modulus: BigInteger, val power: BigInteger) : Serializable {
    internal enum class Operation {
        MULTIPLE, SQUARE
    }

    internal val powerIndexes: List<Operation>

    init {
        powerIndexes = mutableListOf()

        var p0 = power
        while (p0 != ONE) {
            if (p0.mod(TWO) == ZERO) {
                powerIndexes.add(0, Operation.SQUARE)
                p0 = p0.divide(TWO)
            } else {
                powerIndexes.add(0, Operation.MULTIPLE)
                p0 = p0.subtract(ONE)
            }
        }
    }

    override fun toString(): String {
        return String.format("RSAKey{modulus=%s, power=%s}", modulus, power)
    }
}
