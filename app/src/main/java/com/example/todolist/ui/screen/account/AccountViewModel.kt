package com.example.todolist.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.auth.ChangePasswordUseCase
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import com.example.todolist.domain.usecase.widget.GetWidgetEnabledUseCase
import com.example.todolist.domain.usecase.widget.SetWidgetEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase,

    private val getWidgetEnabledUseCase: GetWidgetEnabledUseCase,
    private val setWidgetEnabledUseCase: SetWidgetEnabledUseCase
) : ViewModel() {

    private val _accountUiState = MutableStateFlow<AccountUiState>(AccountUiState.Idle)
    val accountUiState: StateFlow<AccountUiState> = _accountUiState
    val widgetEnabled = getWidgetEnabledUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        loadCurrentUser()
    }

    fun onWidgetToggle(enabled: Boolean) {
        viewModelScope.launch {
            setWidgetEnabledUseCase(enabled)
        }
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
