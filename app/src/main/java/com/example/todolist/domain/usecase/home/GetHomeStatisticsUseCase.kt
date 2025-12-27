package com.example.todolist.domain.usecase.home

import com.example.todolist.domain.model.HomeStatistics
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHomeStatisticsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<HomeStatistics> {
        return taskRepository.getAllTasks(userId).map { tasks ->
            val totalTasks = tasks.size
            val completedTasks = tasks.count { it.isCompleted }
            val remainingTasks = totalTasks - completedTasks
            val progressPercentage = if (totalTasks == 0) 0 else (completedTasks * 100) / totalTasks

            val upcomingTasks = tasks
                .filter { !it.isCompleted && it.dueDate != null }
                .sortedBy { it.dueDate }
                .take(5)

            HomeStatistics(
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                remainingTasks = remainingTasks,
                progressPercentage = progressPercentage,
                upcomingTasks = upcomingTasks,
                allTasks = tasks
            )
        }
    }
}