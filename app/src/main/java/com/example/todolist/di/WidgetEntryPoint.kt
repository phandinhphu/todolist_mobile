package com.example.todolist.di

import com.example.todolist.domain.usecase.task.GetTodayTasksUseCase
import com.example.todolist.domain.usecase.widget.GetWidgetEnabledUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun getTodayTasksUseCase(): GetTodayTasksUseCase
    fun getWidgetEnabledUseCase(): GetWidgetEnabledUseCase
}