package com.ytt.vmv.cryptography

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class EncryptAndSignVote {
    companion object {
        /**
         * Encrypts and signs the vote ([voteOptionNumberInGroup]) for a voter. Returns the [CipherText] and [EncryptProof]
         */
        fun encryptVotes(
            parameters: Parameters,
            electionPublicKey: BigInteger,
            signatureKeyPair: KeyPair,
            voteOptionNumberInGroup: BigInteger
        ): Pair<CipherText, EncryptProof> {
            // Encrypt and sign their plaintext vote as a vote option. We also convert the encrypted vote into a string and put it in the encrypted votes set.
            val (encryptedVote, k) = elGamalEncrypt(
                parameters, electionPublicKey,
                voteOptionNumberInGroup.toByteArray()
            )

            val encryptedVoteSignature = dsaSign(
                parameters,
                signatureKeyPair.privateKey,
                encryptedVote.toByteArray()
            )

            // Create the corresponding proof of knowledge of the encryption. This requires the random value used during the encryption.
            val encryptionSecret = BigInteger(1, k.toByteArray())
            val encryptProof = createEncryptProof(
                parameters,
                electionPublicKey,
                voteOptionNumberInGroup,
                encryptedVote,
                signatureKeyPair,
                encryptedVoteSignature,
                encryptionSecret
            )

            // Verify proof before proceeding as a sanity check.
            if (!verifyEncryptProof(
                    parameters,
                    encryptedVote,
                    signatureKeyPair,
                    encryptProof
                )
            ) {
                throw Exception("Could not verify encryption proofs")
            }

            return Pair(encryptedVote, encryptProof)

            // Create the proof CSV file.
//        val proofFile: File =
//            this.writeCSVToFile(EncryptProof::class.java, encryptProofs, JacksonViews.Public::class.java)
//        this.endProgress()
//        return ProofWrapper(voters, proofFile)

        }

        /**
         * Encrypts the plaintext [data] using the [parameters] and [electionPublicKey].
         */
        private fun elGamalEncrypt(
            parameters: Parameters,
            electionPublicKey: BigInteger,
            data: ByteArray
        ): Pair<CipherText, BigInteger> {
            val (g, p) = parameters

            // Convert the data into a number that should be within the group G of the parameters.
            val numberInGroup = BigInteger(1, data)

            // Generate a random number in the range 1 to p-1.
            val k: BigInteger = generateRandom(p)

            // Calculate alpha as g^k mod p.
            val alpha = g.modPow(k, p)

            // Calculate beta as numberInGroup * h^k mod p, where h is the public key.
            val beta: BigInteger = electionPublicKey.modPow(k, p).multiply(numberInGroup).mod(p)
            return Pair(CipherText(alpha, beta), k)
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

        /**
         * Creates the non-interactive zero-knowledge proofs of knowledge of an ElGamal encryption.
         */
        private fun createEncryptProof(
            parameters: Parameters,
            electionPublicKey: BigInteger,
            plainText: BigInteger,
            cipherText: CipherText,
            signatureKeyPair: KeyPair,
            signature: ByteArray,
            encryptionSecret: BigInteger
        ): EncryptProof {
            val (g, p, q) = parameters

            // Choose a random message in G and and exponent in Zq.
            val random: BigInteger = BigInteger(p.bitLength(), SecureRandom()).mod(p)
            // Use the random number to get an element in the group using the generator g.
            val randomMessage = g.modPow(random, p)
            val randomExponent: BigInteger = BigInteger(q.bitLength(), SecureRandom()).mod(q)

            // Compute c1R = g^randomExponent.
            val c1R = g.modPow(randomExponent, p)

            // Compute c2R = publicKey^randomExponent * randomMessage.
            val c2R: BigInteger =
                electionPublicKey.modPow(randomExponent, p).multiply(randomMessage).mod(p)

            // Form the hash c = H(c1, c2, c1R, c2R, vk, p, q).
            val c: BigInteger = hash(
                q.bitLength(),
                cipherText.alpha,
                cipherText.beta,
                c1R,
                c2R,
                signatureKeyPair.publicKey,
                p,
                q
            )

            // Compute mBar = m^c * randomMessage.
            val mBar = plainText.modPow(c, p).multiply(randomMessage).mod(p)

            // Compute kBar = encryptionSecret * c + randomExponent.
            val kBar = encryptionSecret.multiply(c).add(randomExponent).mod(q)

            // Compute c1Bar = g^kBar.
            val c1Bar = g.modPow(kBar, p)

            // Compute c2Bar = publicKey^kBar * mBar.
            val c2Bar: BigInteger = electionPublicKey.modPow(kBar, p).multiply(mBar).mod(p)

            // Output the proof (c, c1R, c2R, c1Bar, c2Bar, s).
            return EncryptProof(c1R, c2R, c1Bar, c2Bar, signature)
        }

        /**
         * Calculates the hash of the specified [BigInteger] [values] and returns the corresponding [BigInteger].
         * A hash for the specified [bitLength] is used.
         */
        private fun hash(bitLength: Int, vararg values: BigInteger): BigInteger {
            val md = MessageDigest.getInstance("SHA-${shaSuffix(bitLength)}")

            values.forEach { md.update(it.toByteArray()) }
            val digest = md.digest()
            return BigInteger(1, digest)
        }

        /**
         * Verifies the non-interactive zero-knowledge proofs of knowledge of an ElGamal encryption.
         */
        private fun verifyEncryptProof(
            parameters: Parameters, cipherText: CipherText, signatureKeyPair: KeyPair,
            encryptProof: EncryptProof
        ): Boolean {
            val (_, p, q) = parameters

            // Form the hash c = H(c1, c2, c1R, c2R, vk, p, q).
            val c = hash(
                q.bitLength(),
                cipherText.alpha,
                cipherText.beta,
                encryptProof.c1R,
                encryptProof.c2R,
                signatureKeyPair.publicKey,
                p,
                q
            )

            // Verify c1Bar =? c1^c * c1R.
            val c1Bar: BigInteger = cipherText.alpha.modPow(c, p).multiply(encryptProof.c1R).mod(p)
            var result: Boolean = encryptProof.c1Bar == c1Bar

            // Verify c2Bar =? c2^c * c2R.
            val c2Bar: BigInteger = cipherText.beta.modPow(c, p).multiply(encryptProof.c2R).mod(p)
            result = result && (encryptProof.c2Bar == c2Bar)

            // Verify s =? sign(encrypted)
            result = result && dsaVerify(
                parameters,
                signatureKeyPair.publicKey,
                cipherText.toByteArray(),
                encryptProof.encryptedVoteSignature
            )
            return result
        }
    }
}