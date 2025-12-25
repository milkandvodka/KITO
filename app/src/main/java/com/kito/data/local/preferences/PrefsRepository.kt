package com.kito.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_ACADEMIC_YEAR = stringPreferencesKey("academic_year")
        private val KEY_TERM_CODE = stringPreferencesKey("term_code")

        private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        private val KEY_USER_SETUP_DONE = booleanPreferencesKey("user_setup_done")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_ROLLNUMBER = stringPreferencesKey("User_Password")
    }

    val userNameFlow = dataStore.data
        .map { it[KEY_USER_NAME] ?: "" }

    val userRollFlow = dataStore.data
        .map { it[KEY_USER_ROLLNUMBER] ?: "" }

    val academicYearFlow = dataStore.data
        .map { it[KEY_ACADEMIC_YEAR] ?: "" }

    val termCodeFlow = dataStore.data
        .map { it[KEY_TERM_CODE] ?: "" }

    val onBoardingFlow = dataStore.data
        .map { it[KEY_ONBOARDING_DONE] ?: false }

    val userSetupDoneFlow = dataStore.data
        .map { it[KEY_USER_SETUP_DONE] ?: false }

    suspend fun setUserName(username: String) {
        dataStore.edit { it[KEY_USER_NAME] = username }
    }

    suspend fun setUserRollNumber(rollNumber: String) {
        dataStore.edit { it[KEY_USER_ROLLNUMBER] = rollNumber }
    }

    suspend fun setUserSetupDone() {
        dataStore.edit { it[KEY_USER_SETUP_DONE] = true }
    }

    suspend fun setOnboardingDone() {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }

    suspend fun setAcademicYear(year: String) {
        dataStore.edit { it[KEY_ACADEMIC_YEAR] = year }
    }

    suspend fun setTermCode(term: String) {
        dataStore.edit { it[KEY_TERM_CODE] = term }
    }

}