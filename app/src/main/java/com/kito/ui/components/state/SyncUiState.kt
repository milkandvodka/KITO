package com.kito.ui.components.state

sealed class SyncUiState {
    object Idle : SyncUiState()
    object Loading : SyncUiState()
    object Success : SyncUiState()
    data class Error(val message: String) : SyncUiState()
}