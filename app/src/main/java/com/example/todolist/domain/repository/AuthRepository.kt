package com.example.todolist.domain.repository

import com.example.todolist.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String): User
    suspend fun login(email: String, password: String): User
    suspend fun logout(): Unit
    fun getCurrentUser(): User?
    suspend fun changePassword(newPassword: String): Unit
    suspend fun resetPassword(email: String): Unit
}