package com.example.todolist.domain.repository

import kotlinx.coroutines.flow.Flow

interface WidgetSettingsRepository {
    suspend fun setEnabled(enabled: Boolean)
    fun isEnabled(): Flow<Boolean>
}