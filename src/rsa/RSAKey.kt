package rsa

import java.io.Serializable
import java.math.BigInteger
import java.math.BigInteger.*
import java.util.*

class RSAKey internal constructor(val modulus: BigInteger, val power: BigInteger) : Serializable {
    private val indexes: IntArray

    internal val powerIndexes: IntArray
        get() = indexes.clone()

    init {

        val deque = LinkedList<Int>()
        var p0 = power
        while (p0 != ONE) {
            if (p0.mod(TWO) == ZERO) {
                deque.addFirst(2)
                p0 = p0.divide(TWO)
            } else {
                deque.addFirst(1)
                p0 = p0.subtract(ONE)
            }
        }
        this.indexes = deque.stream().mapToInt { i -> i }.toArray()
    }

    override fun toString(): String {
        return String.format("RSAKey{modulus=%s, power=%s}", modulus, power)
    }
}
