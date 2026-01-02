package com.example.todolist.domain.usecase.widget

import com.example.todolist.domain.repository.WidgetSettingsRepository
import javax.inject.Inject

class GetWidgetEnabledUseCase @Inject constructor(
    private val widgetSettingsRepository: WidgetSettingsRepository
) {
    operator fun invoke() = widgetSettingsRepository.isEnabled()
}