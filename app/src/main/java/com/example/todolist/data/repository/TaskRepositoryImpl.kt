package com.example.todolist.data.repository

import com.example.todolist.data.local.database.dao.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun getAllTasks(userId: String): Flow<List<Task>> {
        return taskDao.getAllTasks(userId).map { tasks ->
            tasks.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun toggleTaskComplete(taskId: Long) {
        taskDao.toggleTaskComplete(taskId)
    }
}

