package com.kito.ui.newUi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.remote.SupabaseRepository
import com.kito.data.remote.model.TeacherModel
import com.kito.data.remote.model.TeacherScheduleByIDModel
import com.kito.ui.components.state.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacultyDetailViewModel @Inject constructor(
    private val repository: SupabaseRepository
) : ViewModel() {
    private val _faculty = MutableStateFlow<TeacherModel?>(null)
    val faculty = _faculty.asStateFlow()
    private val _schedule =
        MutableStateFlow<List<TeacherScheduleByIDModel>>(emptyList())
    val schedule = _schedule.asStateFlow()

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState = _syncState.asStateFlow()

    fun loadFacultyDetail(facultyId: Long) {
        viewModelScope.launch {
            _syncState.value = SyncUiState.Loading
            try {
                val facultyDeferred = async {
                    repository.getTeacherDetailByID(facultyId).firstOrNull()
                }
                val scheduleDeferred = async {
                    repository.getTeacherScheduleById(facultyId)
                }
                _faculty.value = facultyDeferred.await()
                _schedule.value = scheduleDeferred.await()
                _syncState.value = SyncUiState.Success
            } catch (e: Exception) {
                Log.d(
                    "FacultyDetail",
                    "FacultyDetailLoadingError: ${e.message ?: ""}"
                )
                _syncState.value = SyncUiState.Error(message = e.message?:"")
            }
        }
    }
}

