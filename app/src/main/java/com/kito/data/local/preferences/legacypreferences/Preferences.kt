package com.kito.data.local.preferences.legacypreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

private const val KEY_USERNAME = "username"
private const val KEY_PASSWORD = "password"
private const val KEY_ACADEMIC_YEAR = "academic_year"
private const val KEY_TERM_CODE = "term_code"

suspend fun Context.getUsername(): String {
    val prefs = dataStore.data.first()
    return prefs[stringPreferencesKey(KEY_USERNAME)] ?: ""
}

suspend fun Context.getPassword(): String {
    val prefs = dataStore.data.first()
    return prefs[stringPreferencesKey(KEY_PASSWORD)] ?: ""
}

suspend fun Context.getAcademicYear(): String {
    val prefs = dataStore.data.first()
    return prefs[stringPreferencesKey(KEY_ACADEMIC_YEAR)] ?: ""
}

suspend fun Context.getTermCode(): String {
    val prefs = dataStore.data.first()
    return prefs[stringPreferencesKey(KEY_TERM_CODE)] ?: ""
}

suspend fun Context.saveCredentials(
    username: String,
    password: String,
    academicYear: String,
    termCode: String
) {
    dataStore.edit { prefs ->
        prefs[stringPreferencesKey(KEY_USERNAME)] = username
        prefs[stringPreferencesKey(KEY_PASSWORD)] = password
        prefs[stringPreferencesKey(KEY_ACADEMIC_YEAR)] = academicYear
        prefs[stringPreferencesKey(KEY_TERM_CODE)] = termCode
    }
}

suspend fun Context.clearCredentials() {
    dataStore.edit { prefs ->
        prefs.remove(stringPreferencesKey(KEY_USERNAME))
        prefs.remove(stringPreferencesKey(KEY_PASSWORD))
        prefs.remove(stringPreferencesKey(KEY_ACADEMIC_YEAR))
        prefs.remove(stringPreferencesKey(KEY_TERM_CODE))
    }
}

suspend fun Context.clearYearAndSession() {
    dataStore.edit { prefs ->
        prefs.remove(stringPreferencesKey(KEY_ACADEMIC_YEAR))
        prefs.remove(stringPreferencesKey(KEY_TERM_CODE))
    }
}

suspend fun Context.hasSavedCredentials(): Boolean {
    val prefs = dataStore.data.first()
    return !prefs[stringPreferencesKey(KEY_USERNAME)].isNullOrEmpty() &&
           !prefs[stringPreferencesKey(KEY_PASSWORD)].isNullOrEmpty() &&
           !prefs[stringPreferencesKey(KEY_ACADEMIC_YEAR)].isNullOrEmpty() &&
           !prefs[stringPreferencesKey(KEY_TERM_CODE)].isNullOrEmpty()
}