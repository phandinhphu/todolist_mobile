package com.example.todolist.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.todolist.data.local.database.dao.TaskDao
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskFilter
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
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
            tagId = filter.tagId,
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

    override suspend fun getAllReminders(): List<Task> {
        return taskDao.getAllReminders(System.currentTimeMillis()).map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTodayTasks(userId: String): List<Task> {
        val zoneId = ZoneId.systemDefault()

        val startOfDay = LocalDate.now()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        val endOfDay = LocalDate.now()
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        return taskDao
            .getTasksForDateRange(userId, startOfDay, endOfDay)
            .map { it.toDomain() }
    }
}

