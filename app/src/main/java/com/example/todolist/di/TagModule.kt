package com.example.todolist.di

import com.example.todolist.data.local.database.AppDatabase
import com.example.todolist.data.local.database.dao.TagDao
import com.example.todolist.data.repository.TagRepositoryImpl
import com.example.todolist.domain.repository.TagRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TagModule {

    @Provides
    fun provideTagDao(database: AppDatabase): TagDao =
        database.tagDao()

    @Provides
    @Singleton
    fun provideTagRepository(tagDao: TagDao): TagRepository =
        TagRepositoryImpl(tagDao)
}