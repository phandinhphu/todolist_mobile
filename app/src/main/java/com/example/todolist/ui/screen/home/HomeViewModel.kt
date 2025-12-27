package com.example.todolist.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.home.GetHomeStatisticsUseCase
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeStatisticsUseCase: GetHomeStatisticsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val currentUserId = MutableStateFlow<String?>(null)

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase()
                if (user != null) {
                    currentUserId.value = user.uid
                    loadHomeStatistics(user.uid)
                } else {
                    _homeUiState.value = HomeUiState.Error("No user logged in")
                }
            } catch (e: Exception) {
                _homeUiState.value = HomeUiState.Error(e.message ?: "Failed to load user")
            }
        }
    }

    private fun loadHomeStatistics(userId: String) {
        viewModelScope.launch {
            try {
                _homeUiState.value = HomeUiState.Loading
                getHomeStatisticsUseCase(userId).collect { statistics ->
                    _homeUiState.value = HomeUiState.Success(statistics)
                }
            } catch (e: Exception) {
                _homeUiState.value = HomeUiState.Error(e.message ?: "Failed to load statistics")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
            } finally {
                currentUserId.value = null
                _homeUiState.value = HomeUiState.Idle
            }
        }
    }

    fun refresh() {
        currentUserId.value?.let { userId ->
            loadHomeStatistics(userId)
        }
    }
}
