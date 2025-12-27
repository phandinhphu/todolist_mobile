package com.example.todolist.ui.screen.home

import com.example.todolist.domain.model.HomeStatistics

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val statistics: HomeStatistics) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
