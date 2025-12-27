package com.example.todolist.domain.repository

import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(userId: String): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Long)
    suspend fun toggleTaskComplete(taskId: Long)
}

