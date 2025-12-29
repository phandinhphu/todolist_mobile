package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.todolist.domain.model.TaskFilter
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String, filter: TaskFilter): Flow<List<Task>> {
        return taskRepository.getAllTasks(userId, filter)
    }
}

