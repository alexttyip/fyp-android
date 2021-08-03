package com.ytt.vmv.cryptography

import java.math.BigInteger
import java.security.SecureRandom

/**
 * Encrypts the plaintext [data] using the [parameters] and [publicKey].
 */
fun elGamalEncrypt(
    parameters: Parameters,
    publicKey: BigInteger,
    data: ByteArray,
): Pair<CipherText, BigInteger> {
    val (g, p) = parameters

    // Convert the data into a number that should be within the group G of the parameters.
    val numberInGroup = BigInteger(1, data)

    // Generate a random number in the range 1 to p-1.
    val k: BigInteger = generateRandom(p)

    // Calculate alpha as g^k mod p.
    val alpha = g.modPow(k, p)

    // Calculate beta as numberInGroup * h^k mod p, where h is the public key.
    val beta: BigInteger = publicKey.modPow(k, p).multiply(numberInGroup).mod(p)

    return CipherText(alpha, beta) to k
}

/**
 * Decrypts the [cipherText] using the [parameters] and [privateKey]
 */
fun elGamalDecrypt(
    parameters: Parameters,
    privateKey: BigInteger,
    cipherText: CipherText,
): BigInteger {
    val (_, p) = parameters

    return cipherText.alpha
        .modPow(p.subtract(BigInteger.ONE).subtract(privateKey), p)
        .multiply(cipherText.beta).mod(p)
}

/**
 * Generate a random number in the range 1 to [limit]-1.
 */
private fun generateRandom(limit: BigInteger): BigInteger {
    val limitBitLength = limit.bitLength()
    var value = BigInteger(limitBitLength, SecureRandom())
    while (value == BigInteger.ZERO || value > limit.subtract(BigInteger.valueOf(2))) {
        value = BigInteger(limitBitLength, SecureRandom())
    }
    return value
}
