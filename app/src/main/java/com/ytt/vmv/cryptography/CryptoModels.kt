package com.ytt.vmv.cryptography

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.ByteBuffer

@Parcelize
data class PublicKeys(
    val publicKeySignature: BigInteger,
    val publicKeyTrapdoor: BigInteger,
) : Parcelable

@Parcelize
data class Parameters(
    val g: BigInteger, // generator
    val p: BigInteger, // prime
    val q: BigInteger, // prime factor of p-1
) : Parcelable

data class KeyPair(
    val privateKey: BigInteger,
    val publicKey: BigInteger,
)

class EncryptProof(
    val c1R: BigInteger,
    val c2R: BigInteger,
    val c1Bar: BigInteger,
    val c2Bar: BigInteger,
    val encryptedVoteSignature: ByteArray,
)

data class CipherText(
    val alpha: BigInteger,
    val beta: BigInteger,
) {
    fun toByteArray(): ByteArray {
        return try {
            // Encode the array as two length (int) and value pairs.
            val alphaBytes = alpha.toByteArray()
            val betaBytes = beta.toByteArray()
            val output = ByteArrayOutputStream()
            output.write(ByteBuffer.allocate(Integer.BYTES).putInt(alphaBytes.size).array())
            output.write(alphaBytes)
            output.write(ByteBuffer.allocate(Integer.BYTES).putInt(betaBytes.size).array())
            output.write(betaBytes)
            output.toByteArray()
        } catch (e: java.lang.Exception) {
            throw Exception("Could not encode ciphertext", e)
        }
    }
}

