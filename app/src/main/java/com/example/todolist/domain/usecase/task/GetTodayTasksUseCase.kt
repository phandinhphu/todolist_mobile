package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.AuthRepository
import com.example.todolist.domain.repository.TaskRepository
import javax.inject.Inject

class GetTodayTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): List<Task> {
        val currentUser = authRepository.getCurrentUser() ?: throw Exception("User not authenticated")
        val allTodayTasks = taskRepository.getTodayTasks(currentUser.uid)
        
        // Lọc task chưa hoàn thành, có deadline, và lấy 3 task có deadline gần nhất
        return allTodayTasks
            .filter { !it.isCompleted && it.dueDate != null }
            .sortedBy { it.dueDate }
            .take(3)
    }
}