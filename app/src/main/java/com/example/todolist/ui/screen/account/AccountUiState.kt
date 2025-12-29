package com.example.todolist.ui.screen.account

import com.example.todolist.domain.model.User

sealed class AccountUiState {
    object Idle : AccountUiState()
    object Loading : AccountUiState()
    object PasswordChanged : AccountUiState()
    data class Error(val message: String) : AccountUiState()
    data class UserLoaded(val user: User) : AccountUiState()
}
