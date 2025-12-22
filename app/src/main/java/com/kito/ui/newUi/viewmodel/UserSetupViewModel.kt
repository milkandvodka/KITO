package com.kito.ui.newUi.viewmodel


import androidx.lifecycle.ViewModel
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserSetupViewModel @Inject constructor(
    private val prefs: PrefsRepository
) : ViewModel(){
    suspend fun setUserName(name: String) {
        val formattedName = name
            .trim()
            .replace("\\s+".toRegex(), " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }

        prefs.setUserName(formattedName)
    }
    suspend fun setUserRoll(roll: String){
        prefs.setUserPassword(roll)
    }
    suspend fun setSapPassword(sapPassword: String){
        prefs.setSapPassword(sapPassword)
    }
    suspend fun setUserSetupDone(){
        prefs.setUserSetupDone()
    }
}