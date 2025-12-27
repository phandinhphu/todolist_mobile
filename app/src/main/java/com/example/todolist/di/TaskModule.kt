package com.example.todolist.di

import com.example.todolist.data.local.database.AppDatabase
import com.example.todolist.data.local.database.dao.TaskDao
import com.example.todolist.data.repository.TaskRepositoryImpl
import com.example.todolist.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TaskModule {

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao =
        database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository =
        TaskRepositoryImpl(taskDao)
}

