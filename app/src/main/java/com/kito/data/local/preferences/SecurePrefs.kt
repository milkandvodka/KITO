package com.kito.data.local.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class SecurePrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private companion object {
        const val KEY_SAP_PASSWORD = "sap_password"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    suspend fun saveSapPassword(password: String) =
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(KEY_SAP_PASSWORD, password)
                .commit()
        }

    fun getSapPassword(): String =
        prefs.getString(KEY_SAP_PASSWORD, "") ?: ""

    suspend fun clearSapPassword() =
        withContext(Dispatchers.IO) {
            prefs.edit()
                .remove(KEY_SAP_PASSWORD)
                .commit()
        }
}