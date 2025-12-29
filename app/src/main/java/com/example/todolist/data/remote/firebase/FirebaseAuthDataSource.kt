package com.example.todolist.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun login(email: String, password: String): FirebaseUser =
        firebaseAuth.signInWithEmailAndPassword(email, password).await().user
            ?: throw Exception("Đăng nhập thất bại")

    suspend fun register(email: String, password: String): FirebaseUser =
        firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
            ?: throw Exception("Đăng ký thất bại")

    suspend fun changePassword(newPassword: String) {
        val user = firebaseAuth.currentUser
            ?: throw Exception("Người dùng chưa đăng nhập")
        user.updatePassword(newPassword).await()
    }

    suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun logout() = firebaseAuth.signOut()
}