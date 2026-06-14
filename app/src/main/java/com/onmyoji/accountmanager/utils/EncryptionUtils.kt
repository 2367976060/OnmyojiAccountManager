package com.onmyoji.accountmanager.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val SECRET_KEY = "OnmyojiAccountManagerSecretKey2024"
    private const val SALT = "OnmyojiSalt2024"
    private const val ITERATION_COUNT = 1000
    private const val KEY_LENGTH = 256

    private fun generateKey(): SecretKeySpec {
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(SECRET_KEY.toCharArray(), SALT.toByteArray(), ITERATION_COUNT, KEY_LENGTH)
        val tmp = keyFactory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    fun encrypt(input: String): String {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, generateKey())
            val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return input
        }
    }

    fun decrypt(input: String): String {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, generateKey())
            val decoded = Base64.decode(input, Base64.NO_WRAP)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            return input
        }
    }
}
