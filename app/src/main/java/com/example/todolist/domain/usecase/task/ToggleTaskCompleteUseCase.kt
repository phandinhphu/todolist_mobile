package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskCompleteUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        taskRepository.toggleTaskComplete(taskId)
    }
}

