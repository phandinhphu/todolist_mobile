package com.example.todolist.domain.usecase.auth

import com.example.todolist.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String) {
        authRepository.changePassword(newPassword)
    }
}