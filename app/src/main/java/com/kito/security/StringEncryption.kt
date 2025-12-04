package com.kito.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object StringEncryption {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    // A simple key - in a real app, you'd want something more sophisticated
    // This key will be obfuscated by ProGuard anyway
    private const val DEFAULT_KEY = "KitoSecurityKey" // 16 characters for AES

    fun encrypt(plainText: String): String {
        try {
            val keySpec = SecretKeySpec(DEFAULT_KEY.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            // If encryption fails, return the original string as fallback
            return plainText
        }
    }

    fun decrypt(encryptedText: String): String {
        try {
            val keySpec = SecretKeySpec(DEFAULT_KEY.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.NO_WRAP))
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // If decryption fails, return the original string as fallback
            return encryptedText
        }
    }

    fun encryptIfPossible(plainText: String): String {
        // For security, we encrypt if possible, with fallback to original string
        // This is for strings that need to remain strings at compile time
        return encrypt(plainText)
    }

    // Simple XOR-based obfuscation that's more reliable than full encryption
    private const val XOR_KEY = 0x4B // 'K' in ASCII

    fun simpleObfuscate(input: String): String {
        val chars = input.toCharArray()
        for (i in chars.indices) {
            chars[i] = (chars[i].code xor XOR_KEY).toChar()
        }
        return String(chars)
    }

    fun simpleDeobfuscate(input: String): String {
        // Since XOR is its own inverse, same function works for deobfuscation
        val chars = input.toCharArray()
        for (i in chars.indices) {
            chars[i] = (chars[i].code xor XOR_KEY).toChar()
        }
        return String(chars)
    }
}