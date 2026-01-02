package com.example.todolist.domain.usecase.widget

import com.example.todolist.domain.repository.WidgetSettingsRepository
import javax.inject.Inject

class SetWidgetEnabledUseCase @Inject constructor(
    private val widgetSettingsRepository: WidgetSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        widgetSettingsRepository.setEnabled(enabled)
    }
}