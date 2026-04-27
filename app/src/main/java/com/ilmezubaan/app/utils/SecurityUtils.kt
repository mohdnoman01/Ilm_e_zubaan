package com.ilmezubaan.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {
    fun getEncryptedPrefs(context: Context): SharedPreferences? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "auth_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to regular SharedPreferences if encryption fails (e.g. Keystore issues)
            context.getSharedPreferences("auth_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    fun hashPassword(password: String, salt: ByteArray = SecureRandom().generateSeed(16)): Pair<String, String> {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = Base64.encodeToString(md.digest(password.toByteArray()), Base64.DEFAULT)
        val saltStr = Base64.encodeToString(salt, Base64.DEFAULT)
        return Pair(hashedPassword, saltStr)
    }
    
    fun verifyPassword(password: String, hashedPassword: String, saltStr: String): Boolean {
        val salt = Base64.decode(saltStr, Base64.DEFAULT)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val newHashedPassword = Base64.encodeToString(md.digest(password.toByteArray()), Base64.DEFAULT)
        return newHashedPassword == hashedPassword
    }
}
