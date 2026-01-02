package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.repository.TaskRepository
import com.example.todolist.util.WidgetUpdateHelper
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val widgetUpdateHelper: WidgetUpdateHelper
) {
    suspend operator fun invoke(taskId: Long) {
        taskRepository.deleteTask(taskId)
        widgetUpdateHelper.updateWidget()
    }
}

