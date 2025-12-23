package com.kito.data.local.preferences.newpreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_ACADEMIC_YEAR = stringPreferencesKey("academic_year")
        private val KEY_TERM_CODE = stringPreferencesKey("term_code")

        private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        private val KEY_USER_SETUP_DONE = booleanPreferencesKey("user_setup_done")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_ROLLNUMBER = stringPreferencesKey("User_Password")
        private val KEY_SAP_PASSWORD = stringPreferencesKey("Sap_Password")
    }

    val userNameFlow = dataStore.data
        .map { it[KEY_USER_NAME] ?: "" }

    val userRollFlow = dataStore.data
        .map { it[KEY_USER_ROLLNUMBER] ?: "" }

    val sapPasswordFlow = dataStore.data
        .map { it[KEY_SAP_PASSWORD] ?: "" }

    val sapLoggedInFlow = sapPasswordFlow
        .map { it.isNotEmpty() }

    val academicYearFlow = dataStore.data
        .map { it[KEY_ACADEMIC_YEAR] ?: "" }

    val termCodeFlow = dataStore.data
        .map { it[KEY_TERM_CODE] ?: "" }


    suspend fun getUserName(): String {
        val prefs = dataStore.data.first()
        return prefs[KEY_USER_NAME] ?: ""
    }
    suspend fun setUserName(username: String) {
        dataStore.edit { it[KEY_USER_NAME] = username }
    }
    suspend fun getUserROllNumber(): String {
        val prefs = dataStore.data.first()
        return prefs[KEY_USER_ROLLNUMBER] ?: ""
    }
    suspend fun setUserRollNumber(rollNumber: String) {
        dataStore.edit { it[KEY_USER_ROLLNUMBER] = rollNumber }
    }
    suspend fun getSapPassword(): String {
        val prefs = dataStore.data.first()
        return prefs[KEY_SAP_PASSWORD] ?: ""
    }
    suspend fun setSapPassword(password: String) {
        dataStore.edit { it[KEY_SAP_PASSWORD] = password }
    }
    suspend fun isUserSetupDone(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[KEY_USER_SETUP_DONE] ?: false
    }
    suspend fun setUserSetupDone() {
        dataStore.edit { it[KEY_USER_SETUP_DONE] = true }
    }

    suspend fun isOnboardingDone(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[KEY_ONBOARDING_DONE] ?: false
    }

    suspend fun setOnboardingDone() {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }

    suspend fun getUsername() = dataStore.data.first()[KEY_USERNAME] ?: ""

    suspend fun getPassword() = dataStore.data.first()[KEY_PASSWORD] ?: ""

    suspend fun getAcademicYear() = dataStore.data.first()[KEY_ACADEMIC_YEAR] ?: ""

    suspend fun getTermCode() = dataStore.data.first()[KEY_TERM_CODE] ?: ""

    suspend fun saveCredentials(
        username: String,
        password: String,
        academicYear: String,
        termCode: String
    ) {
        dataStore.edit {
            it[KEY_USERNAME] = username
            it[KEY_PASSWORD] = password
            it[KEY_ACADEMIC_YEAR] = academicYear
            it[KEY_TERM_CODE] = termCode
        }
    }

    suspend fun clearCredentials() {
        dataStore.edit {
            it.remove(KEY_USERNAME)
            it.remove(KEY_PASSWORD)
            it.remove(KEY_ACADEMIC_YEAR)
            it.remove(KEY_TERM_CODE)
        }
    }

    suspend fun clearYearAndSession() {
        dataStore.edit {
            it.remove(KEY_ACADEMIC_YEAR)
            it.remove(KEY_TERM_CODE)
        }
    }

    suspend fun hasSavedCredentials(): Boolean {
        val prefs = dataStore.data.first()
        return !prefs[KEY_USERNAME].isNullOrEmpty() &&
                !prefs[KEY_PASSWORD].isNullOrEmpty() &&
                !prefs[KEY_ACADEMIC_YEAR].isNullOrEmpty() &&
                !prefs[KEY_TERM_CODE].isNullOrEmpty()
    }
}