package com.example.todolist.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.todolist.domain.repository.WidgetSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WidgetSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : WidgetSettingsRepository {
    private val KEY_WIDGET_ENABLED = booleanPreferencesKey("widget_enabled")

    override fun isEnabled(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_WIDGET_ENABLED] ?: false
        }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_WIDGET_ENABLED] = enabled
        }
    }
}