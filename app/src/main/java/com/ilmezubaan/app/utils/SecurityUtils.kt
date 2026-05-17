package com.ilmezubaan.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.MessageDigest
import java.security.GeneralSecurityException
import java.security.SecureRandom

object SecurityUtils {
    private const val TAG = "SecurityUtils"
    private const val ENCRYPTED_PREFS_NAME = "auth_prefs_encrypted"
    private const val FALLBACK_PREFS_NAME = "auth_prefs_v2"

    fun getEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            migrateLegacyPrefsIfNeeded(
                legacyPrefs = context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE),
                encryptedPrefs = encryptedPrefs
            )
            encryptedPrefs
        } catch (e: GeneralSecurityException) {
            Log.w(TAG, "Encrypted preferences unavailable; using device-local fallback.", e)
            context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
        } catch (e: IOException) {
            Log.w(TAG, "Encrypted preferences unavailable; using device-local fallback.", e)
            context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun migrateLegacyPrefsIfNeeded(
        legacyPrefs: SharedPreferences,
        encryptedPrefs: SharedPreferences
    ) {
        if (legacyPrefs.all.isEmpty() || encryptedPrefs.all.isNotEmpty()) return

        val editor = encryptedPrefs.edit()
        legacyPrefs.all.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    editor.putStringSet(key, value as Set<String>)
                }
            }
        }
        editor.apply()
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
