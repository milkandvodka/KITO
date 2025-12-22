package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val prefs: PrefsRepository
): ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _sapLoggedIn = MutableStateFlow(false)
    val sapLoggedIn = _sapLoggedIn.asStateFlow()


    init {
        viewModelScope.launch {
            _name.value = prefs.getUserName()
            _sapLoggedIn.value = prefs.getSapPassword().isNotEmpty()
        }
    }
}