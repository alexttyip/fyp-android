package com.ytt.vmv.cryptography

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ytt.vmv.R
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.spec.DSAParameterSpec

class VoterKeyGenerator {
    companion object {
        enum class PrivateKey {
            SIGNATURE_PRIVATE_KEY,
            TRAPDOOR_PRIVATE_KEY
        }

        fun genAndStore(
            applicationContext: Context,
            electionName: String,
            g: BigInteger,
            p: BigInteger,
            q: BigInteger
        ): PublicKeys {
            val spec = DSAParameterSpec(p, q, g)
            val generator = KeyPairGenerator.getInstance("DSA")

            generator.initialize(spec, SecureRandom())

            val trapdoor = generator.genKeyPair()
            val signing = generator.genKeyPair()

            val trapdoorPrivateKey = (trapdoor.private as DSAPrivateKey).x
            val trapdoorPublicKey = (trapdoor.public as DSAPublicKey).y
            val signingPrivateKey = (signing.private as DSAPrivateKey).x
            val signingPublicKey = (signing.public as DSAPublicKey).y

            val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPrefsFile: String =
                electionName + applicationContext.getString(R.string.preference_private_keys_suffix)

            val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
                applicationContext,
                sharedPrefsFile,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            with(sharedPreferences.edit()) {
                putString(
                    PrivateKey.TRAPDOOR_PRIVATE_KEY.name,
                    trapdoorPrivateKey.toString()
                )

                putString(
                    PrivateKey.SIGNATURE_PRIVATE_KEY.name,
                    signingPrivateKey.toString()
                )

                apply()
            }

            /* https://developer.android.com/training/articles/keystore#UserAuthentication
            val advancedSpec = KeyGenParameterSpec.Builder(
                "master_key",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(256)
                setUserAuthenticationRequired(true)
                setUserAuthenticationValidityDurationSeconds(15) // must be larger than 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setUnlockedDeviceRequired(true)
                    setIsStrongBoxBacked(true)
                }
            }.build()

            val advancedKeyAlias = MasterKeys.getOrCreate(advancedSpec) */

            return PublicKeys(trapdoorPublicKey, signingPublicKey)
        }

        fun getPrivateKey(
            applicationContext: Context,
            electionName: String,
            privateKey: PrivateKey,
        ): BigInteger {
            val mainKey = MasterKey.Builder(applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPrefsFile: String =
                electionName + applicationContext.getString(R.string.preference_private_keys_suffix)

            val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
                applicationContext,
                sharedPrefsFile,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return BigInteger(sharedPreferences.getString(privateKey.name, "-1")!!)
        }
    }
}
