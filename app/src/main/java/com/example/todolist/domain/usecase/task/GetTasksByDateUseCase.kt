package com.example.todolist.domain.usecase.task

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.AuthRepository
import com.example.todolist.domain.repository.TaskRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class GetTasksByDateUseCase
@Inject
constructor(
        private val taskRepository: TaskRepository,
        private val authRepository: AuthRepository
) {
    suspend operator fun invoke(date: LocalDate): List<Task> {
        val currentUser =
                authRepository.getCurrentUser() ?: throw Exception("User not authenticated")

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay =
                date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return taskRepository.getTasksByDueDate(currentUser.uid, startOfDay, endOfDay)
    }
}
