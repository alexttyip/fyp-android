package com.ytt.vmv.cryptography

import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.params.DSAParameters
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters
import org.bouncycastle.crypto.params.DSAPublicKeyParameters
import org.bouncycastle.crypto.signers.DSADigestSigner
import org.bouncycastle.crypto.signers.DSASigner
import org.bouncycastle.crypto.util.DigestFactory

/**
 * Signs the specified data using the [parameters] and [keyPair].
 */
fun sign(parameters: Parameters, keyPair: KeyPair, data: ByteArray): ByteArray? {
    return try {
        println("DSA sign")

        // Convert the DH parameters.
        val dsaParameters = DSAParameters(parameters.p, parameters.q, parameters.g)
        val hashLength = dsaParameters.q.bitLength()

        // Generate the signature.
        val signer = DSADigestSigner(DSASigner(), digestForLength(hashLength))
        val privateKeyParameters = DSAPrivateKeyParameters(keyPair.privateKey, dsaParameters)
        signer.init(true, privateKeyParameters)
        signer.update(data, 0, data.size)
        signer.generateSignature()
    } catch (e: Exception) {
        throw Exception("Could not DSA sign", e)
    }
}

/**
 * Verifies the signature of the data using the [parameters] and [keyPair].
 */
fun verify(
    parameters: Parameters,
    keyPair: KeyPair,
    data: ByteArray,
    signature: ByteArray?
): Boolean {
    return try {
        println("DSA verify")

        // Convert the DH parameters.
        val dsaParameters = DSAParameters(parameters.p, parameters.q, parameters.g)
        val hashLength = dsaParameters.q.bitLength()

        // Verify the signature.
        val signer = DSADigestSigner(DSASigner(), digestForLength(hashLength))
        val publicKeyParameters = DSAPublicKeyParameters(keyPair.publicKey, dsaParameters)
        signer.init(false, publicKeyParameters)
        signer.update(data, 0, data.size)
        signer.verifySignature(signature)
    } catch (e: Exception) {
        throw Exception("Could not DSA sign", e)
    }
}

/**
 * Creates a digest for the required bit [length]. Works up to 512 bits.
 */
private fun digestForLength(length: Int): Digest {
    return when {
        length <= 160 -> DigestFactory.createSHA1()
        length <= 256 -> DigestFactory.createSHA256()
        length <= 384 -> DigestFactory.createSHA384()
        else -> DigestFactory.createSHA512()
    }
}
