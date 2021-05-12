package com.ytt.vmv.cryptography

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters
import org.bouncycastle.crypto.params.DSAParameters
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters
import org.bouncycastle.crypto.params.DSAPublicKeyParameters
import java.math.BigInteger
import java.security.SecureRandom

/**
 * Generates a voter's trapdoor and signing key pairs with election [parameters].
 */
fun generate(parameters: Parameters): Pair<KeyPair, KeyPair> {
    // Create the voter's trapdoor and signature key pairs.
    println("Create Voter Trapdoor Keys")
    val trapdoor = createKeys(parameters)

    println("Create Voter Trapdoor Keys")
    val signing = createKeys(parameters)

    return Pair(trapdoor, signing)
}

/**
 * Creates a DSA key pair with the provided [parameters].
 */
private fun createKeys(parameters: Parameters): KeyPair {
    // Convert the DH parameters.
    val dsaParameters = DSAParameters(parameters.p, parameters.q, parameters.g)

    // Generate the key pair using the parameters.
    val generator = DSAKeyPairGenerator()
    generator.init(DSAKeyGenerationParameters(SecureRandom(), dsaParameters))

    println("Generating DSA keys")

    val keyPair: AsymmetricCipherKeyPair = generator.generateKeyPair()
    return KeyPair(
        (keyPair.private as DSAPrivateKeyParameters).x,
        (keyPair.public as DSAPublicKeyParameters).y
    )
}

data class KeyPair(
    val privateKey: BigInteger,
    val publicKey: BigInteger,
)

data class Parameters(
//        var name: String,
//        var numTellers: Int,
//        var thresholdTellers: Int,
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger // prime factor of p-1
)

