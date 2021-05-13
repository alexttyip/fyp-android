package com.ytt.vmv.cryptography

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters
import org.bouncycastle.crypto.params.DSAParameters
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters
import org.bouncycastle.crypto.params.DSAPublicKeyParameters
import java.math.BigInteger
import java.security.SecureRandom

class VoterKeyGenerator {
    companion object {
        /**
         * Generates a voter's trapdoor and signing key pairs with election parameters [g], [p], and [q].
         *
         * Returns trapdoorPublic, trapdoorPrivate, signingPublic, and signingPrivate
         */
        fun generate(
            g: BigInteger,
            p: BigInteger,
            q: BigInteger
        ): Keys {
            // Create the voter's trapdoor and signature key pairs.
            println("Create Voter Trapdoor Keys")
            val trapdoor = createKeys(g, p, q)

            println("Create Voter Trapdoor Keys")
            val signing = createKeys(g, p, q)

            return Keys(
                trapdoor.first, trapdoor.second, signing.first, signing.second
            )
        }

        /**
         * Creates a DSA key pair with the provided parameters [g], [p], and [q].
         */
        private fun createKeys(
            g: BigInteger,
            p: BigInteger,
            q: BigInteger
        ): Pair<BigInteger, BigInteger> {
            // Convert the DH parameters.
            val dsaParameters = DSAParameters(p, q, g)

            // Generate the key pair using the parameters.
            val generator = DSAKeyPairGenerator()
            generator.init(DSAKeyGenerationParameters(SecureRandom(), dsaParameters))

            println("Generating DSA keys")

            val keyPair: AsymmetricCipherKeyPair = generator.generateKeyPair()
            return Pair(
                (keyPair.private as DSAPrivateKeyParameters).x,
                (keyPair.public as DSAPublicKeyParameters).y
            )
        }
    }
}
