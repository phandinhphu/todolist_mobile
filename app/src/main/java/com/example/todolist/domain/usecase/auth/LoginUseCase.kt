package com.example.todolist.domain.usecase.auth

import com.example.todolist.domain.model.User
import com.example.todolist.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User {
        return authRepository.login(email, password)
    }
}