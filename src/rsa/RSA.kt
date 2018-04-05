package rsa

import rsa.RSAKey.Operation.MULTIPLE
import java.io.*
import java.math.BigInteger

class RSA(val key: RSAKey) {
    private fun operateOn(integer: BigInteger): BigInteger {
        if (integer > key.modulus) {
            throw IllegalArgumentException("Too Big.")
        } else {
            var result = integer
            for (i in key.powerIndexes) {
                when (i) {
                    MULTIPLE -> result = result.multiply(integer).mod(key.modulus)
                    else -> result = result.multiply(result).mod(key.modulus)
                }
            }
            return result
        }
    }

    /**
     * 用RSA算法加密一个long型整数。
     *
     * @param l 一个非负整数
     * @return 加密结果
     */
    fun encryptLong(l: Long): BigInteger {
        return operateOn(BigInteger.valueOf(l))
    }

    /**
     * 用RSA算法加密一个可序列化对象，具体做法是现将该对象序列化为一个byte数组，再对每一个byte使用RSA算法，最后将加密结果序列化返回。
     */
    fun encryptObj(obj: Serializable): ByteArray {
        val list = mutableListOf<BigInteger>()

        ByteArrayOutputStream().use { buf ->
            ObjectOutputStream(buf).use { out ->
                out.writeObject(obj)
                out.flush()

                // Todo: more efficiently encode bytes
                for (i in buf.toByteArray().convertToPositiveInt()) {
                    list.add(encryptLong(i.toLong()))
                }
            }
        }

        ByteArrayOutputStream().use { buf ->
            ObjectOutputStream(buf).use { out ->
                out.writeObject(list)
                out.flush()

                return buf.toByteArray()
            }
        }
    }

    /**
     * 解密使用方法 [RSA.encryptObj] 加密的对象，需保证本实例包含的 [RSAKey] 与加密时使用的 [RSAKey] 匹配。
     */
    fun <T : Serializable> decryptObj(content: ByteArray): T {
        ByteArrayInputStream(content).use { buf ->
            ObjectInputStream(buf).use { stream ->
                val list = (stream.readObject() as List<*>).filterIsInstance<BigInteger>()
                val integers = list.map { operateOn(it).toInt() }

                ObjectInputStream(ByteArrayInputStream(integers.convertToBytes())).use { input ->
                    return input.readObject() as T
                }
            }
        }
    }
}

