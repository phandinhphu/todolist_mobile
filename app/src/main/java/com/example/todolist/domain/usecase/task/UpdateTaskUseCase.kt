package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task)
    }
}

