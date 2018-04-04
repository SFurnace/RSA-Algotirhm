package rsa;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import static rsa.FunctionUtils.convertBytesToIntegers;
import static rsa.FunctionUtils.convertIntegersToBytes;


public class RSA {
    private RSAKey key;

    public RSA(RSAKey key) {
        this.key = key;
    }

    public RSAKey getKey() {
        return key;
    }

    private BigInteger encrypt(BigInteger integer) {
        if (integer.compareTo(key.getModulus()) > 0) {
            throw new IllegalArgumentException("Too Big.");
        } else {
            int[] indexes = key.getPowerIndexes();
            BigInteger modulus = key.getModulus();
            BigInteger result = integer;

            for (int i : indexes) {
                if (i == 1) {
                    result = result.multiply(integer).mod(modulus);
                } else {
                    result = result.multiply(result).mod(modulus);
                }
            }

            return result;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public BigInteger encryptLong(long l) {
        return encrypt(BigInteger.valueOf(l));
    }

    public byte[] encryptObj(Serializable obj) throws IOException {
        ArrayList<BigInteger> list = new ArrayList<>();

        try (ByteArrayOutputStream buf = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(buf)) {
            out.writeObject(obj);
            out.flush();

            byte[] bytes = buf.toByteArray();

            // Todo: more efficiently encode bytes
            for (int b : convertBytesToIntegers(bytes)) {
                list.add(encryptLong(b));
            }
        }

        try (ByteArrayOutputStream buf = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(buf)) {
            out.writeObject(list);
            out.flush();

            return buf.toByteArray();
        }
    }

    public <T extends Serializable> T decryptObj(byte[] content) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream buf = new ByteArrayInputStream(content);
             ObjectInputStream stream = new ObjectInputStream(buf)) {
            ArrayList list = (ArrayList) stream.readObject();

            int[] integers = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                integers[i] = encrypt((BigInteger) list.get(i)).byteValue();
            }

            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(convertIntegersToBytes(integers)))) {
                Object result = in.readObject();
                return (T) result;
            }
        }
    }
}

