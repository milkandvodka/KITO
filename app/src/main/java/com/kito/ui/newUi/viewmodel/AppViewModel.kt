package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.preferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val pref: PrefsRepository,
    private val sectionRepository: SectionRepository
): ViewModel() {
    fun checkResetFix(){
        viewModelScope.launch {
            val isResetFixDone = pref.resetFixFlow.first()
            if(!isResetFixDone){
                sectionRepository.deleteAllSection()
                pref.setResetDone()
            }
        }
    }
}