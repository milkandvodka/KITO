package com.kito.ui.newUi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.remote.SupabaseRepository
import com.kito.data.remote.model.TeacherFuzzySearchModel
import com.kito.data.remote.model.TeacherModel
import com.kito.ui.components.ConnectivityObserver
import com.kito.ui.components.state.SearchResultState
import com.kito.ui.components.state.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacultyScreenViewModel @Inject constructor(
    private val repository: SupabaseRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val isOnline = connectivityObserver.isOnline

    private val _faculty = MutableStateFlow<List<TeacherModel>>(emptyList())
    val faculty = _faculty.asStateFlow()

    private val _searchResultState =
        MutableStateFlow<SearchResultState>(SearchResultState.Idle)
    val searchResultState = _searchResultState.asStateFlow()

    private val _facultySearchResult =
        MutableStateFlow<List<TeacherFuzzySearchModel>>(emptyList())
    val facultySearchResult = _facultySearchResult.asStateFlow()

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState = _syncState.asStateFlow()

    init {
        viewModelScope.launch {
            isOnline.collect { online ->
                if (!online) {
                    _syncState.value = SyncUiState.Idle
                    _faculty.value = emptyList()
                } else {
                    fetchFaculty()
                }
            }
        }
    }

    private suspend fun fetchFaculty() {
        _syncState.value = SyncUiState.Loading
        try {
            _faculty.value = repository.getAllTeacherDetail()
            _syncState.value = SyncUiState.Success
        } catch (e: Exception) {
            _syncState.value =
                SyncUiState.Error(e.message ?: "Failed to load faculty")
        }
    }

    fun retry() {
        viewModelScope.launch {
            if (isOnline.value) {
                fetchFaculty()
            } else {
                _syncState.value = SyncUiState.Idle
            }
        }
    }

    fun getSearchResult(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _facultySearchResult.value = emptyList()
                _searchResultState.value = SearchResultState.Idle
            } else {
                val result = repository.getTeacherSearchResponse(query)
                _facultySearchResult.value = result
                _searchResultState.value =
                    if (result.isEmpty()) SearchResultState.Empty
                    else SearchResultState.Success
            }
        }
    }

    fun clearSearchResult() {
        _facultySearchResult.value = emptyList()
        _searchResultState.value = SearchResultState.Idle
    }
}