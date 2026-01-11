package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.remote.SupabaseRepository
import com.kito.data.remote.model.TeacherModel
import com.kito.data.remote.model.TeacherScheduleByIDModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun loadFacultyDetail(facultyId: Long) {
        viewModelScope.launch {
            _faculty.value =
                repository.getTeacherDetailByID(facultyId).firstOrNull()
            _schedule.value =
                repository.getTeacherScheduleById(facultyId)
        }
    }
}

