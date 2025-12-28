package com.example.todolist.data.repository

import com.example.todolist.data.local.database.dao.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskFilter
import com.example.todolist.domain.model.TaskCategory // Thêm dòng này để hết lỗi CharCategory
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun getAllTasks(userId: String, filter: TaskFilter): Flow<List<Task>> {
        return taskDao.getFilteredTasks(
            userId = userId,
            searchQuery = filter.searchQuery,
            category = filter.category?.name,
            priority = filter.priority?.name,
            isCompleted = filter.isCompleted
        ).map { entities ->
            entities.map { it.toDomain() }
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

