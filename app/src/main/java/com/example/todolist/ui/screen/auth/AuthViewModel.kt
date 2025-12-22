package com.example.todolist.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LoginUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import com.example.todolist.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val user = loginUseCase(email, password)
                _authUiState.value = AuthUiState.Success(user)
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Login error")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val user = registerUseCase(email, password)
                _authUiState.value = AuthUiState.Success(user)
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Registration error")
            }
        }
    }

    fun getCurrentUser() {
        val user = getCurrentUserUseCase()
        if (user != null) {
            _authUiState.value = AuthUiState.Success(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authUiState.value = AuthUiState.Idle
        }
    }
}