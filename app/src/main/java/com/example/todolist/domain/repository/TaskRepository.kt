package com.example.todolist.domain.repository

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskFilter
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(userId: String, filter: TaskFilter): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Long)
    suspend fun toggleTaskComplete(taskId: Long)
    suspend fun getAllReminders(): List<Task>
    suspend fun getTodayTasks(userId: String): List<Task>
    suspend fun getTasksByDueDate(userId: String, startOfDay: Long, endOfDay: Long): List<Task>
}
