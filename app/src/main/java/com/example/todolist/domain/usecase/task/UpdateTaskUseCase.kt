package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import com.example.todolist.util.WidgetUpdateHelper
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val widgetUpdateHelper: WidgetUpdateHelper
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task)
        widgetUpdateHelper.updateWidget()
    }
}

