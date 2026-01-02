package com.example.todolist.di

import com.example.todolist.data.repository.WidgetSettingsRepositoryImpl
import com.example.todolist.domain.repository.WidgetSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {

    @Binds
    abstract fun bindWidgetSettingsRepository(
        impl: WidgetSettingsRepositoryImpl
    ): WidgetSettingsRepository

}
