package com.example.todolist.domain.usecase.auth

import com.example.todolist.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() : Unit {
        return authRepository.logout()
    }
}