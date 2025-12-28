package com.example.todolist.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.auth.ChangePasswordUseCase
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _accountUiState = MutableStateFlow<AccountUiState>(AccountUiState.Idle)
    val accountUiState: StateFlow<AccountUiState> = _accountUiState

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        val user = getCurrentUserUseCase()
        if (user != null) {
            _accountUiState.value = AccountUiState.UserLoaded(user)
        } else {
            _accountUiState.value = AccountUiState.Error("Không tìm thấy thông tin người dùng")
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            _accountUiState.value = AccountUiState.Loading
            try {
                changePasswordUseCase(newPassword)
                _accountUiState.value = AccountUiState.PasswordChanged
            } catch (e: Exception) {
                _accountUiState.value = AccountUiState.Error(e.message ?: "Lỗi khi đổi mật khẩu")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _accountUiState.value = AccountUiState.Idle
        }
    }

    fun resetState() {
        loadCurrentUser()
    }
}
