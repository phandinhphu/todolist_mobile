package com.example.todolist.domain.usecase.auth

import com.example.todolist.domain.model.User
import com.example.todolist.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}