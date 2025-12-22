package com.example.todolist.data.repository

import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.remote.firebase.FirebaseAuthDataSource
import com.example.todolist.domain.model.User
import com.example.todolist.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
): AuthRepository {
    override suspend fun register(
        email: String,
        password: String
    ): User {
        val firebaseUser = firebaseAuthDataSource.register(email, password)
        return firebaseUser.toDomain()
    }

    override suspend fun login(
        email: String,
        password: String
    ): User {
        val firebaseUser = firebaseAuthDataSource.login(email, password)
        return firebaseUser.toDomain()
    }

    override suspend fun logout() {
        firebaseAuthDataSource.logout()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuthDataSource.getCurrentUser()
        return firebaseUser?.toDomain()
    }
}