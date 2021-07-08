package com.ytt.vmv.cryptography

import java.math.BigInteger
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.DSAPrivateKeySpec
import java.security.spec.DSAPublicKeySpec

/**
 * Signs the specified data using the [parameters] and [privateKey].
 */
fun dsaSign(parameters: Parameters, privateKey: BigInteger, data: ByteArray): ByteArray {
    val (g, p, q) = parameters
    val spec = DSAPrivateKeySpec(privateKey, p, q, g)
    val factory = KeyFactory.getInstance("DSA")
    val myPrivateKey = factory.generatePrivate(spec)
    val sign = Signature.getInstance("SHA${shaSuffix(q.bitLength())}withDSA")

    sign.initSign(myPrivateKey)
    sign.update(data)

    return sign.sign()
}

/**
 * Verifies the signature of the data using the [parameters] and [publicKey].
 */
fun dsaVerify(
    parameters: Parameters,
    publicKey: BigInteger,
    data: ByteArray,
    signature: ByteArray?
): Boolean {
    val (g, p, q) = parameters
    val spec = DSAPublicKeySpec(publicKey, p, q, g)
    val factory = KeyFactory.getInstance("DSA")
    val myPublicKey = factory.generatePublic(spec)
    val verifier = Signature.getInstance("SHA${shaSuffix(q.bitLength())}withDSA")

    verifier.initVerify(myPublicKey)
    verifier.update(data)

    return verifier.verify(signature)
}

/**
 * Creates a digest for the required bit [length]. Works up to 512 bits.
 */
internal fun shaSuffix(length: Int) = when {
    length <= 160 -> "1"
    length <= 256 -> "256"
    length <= 384 -> "384"
    else -> "512"
}
