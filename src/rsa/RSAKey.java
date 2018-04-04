package rsa;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Deque;
import java.util.LinkedList;

import static java.math.BigInteger.*;

public class RSAKey implements Serializable {
    private BigInteger modulus;
    private BigInteger power;
    private int[] indexes;

    RSAKey(BigInteger modulus, BigInteger power) {
        this.modulus = modulus;
        this.power = power;

        Deque<Integer> deque = new LinkedList<>();
        BigInteger p0 = power;
        while (!p0.equals(ONE)) {
            if (p0.mod(TWO).equals(ZERO)) {
                deque.addFirst(2);
                p0 = p0.divide(TWO);
            } else {
                deque.addFirst(1);
                p0 = p0.subtract(ONE);
            }
        }
        this.indexes = deque.stream().mapToInt(i -> i).toArray();
    }

    int[] getPowerIndexes() {
        return indexes.clone();
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPower() {
        return power;
    }

    @Override
    public String toString() {
        return String.format("RSAKey{modulus=%s, power=%s}", modulus, power);
    }
}
