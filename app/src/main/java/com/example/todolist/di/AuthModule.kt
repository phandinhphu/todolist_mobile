package com.example.todolist.di

import com.example.todolist.data.remote.firebase.FirebaseAuthDataSource
import com.example.todolist.data.repository.AuthRepositoryImpl
import com.example.todolist.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource =
        FirebaseAuthDataSource(firebaseAuth)

    @Provides
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository =
        AuthRepositoryImpl(dataSource)
}