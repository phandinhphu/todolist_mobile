package com.example.todolist.data.local.database.dao

import androidx.room.*
import com.example.todolist.data.local.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = NOT isCompleted WHERE id = :taskId")
    suspend fun toggleTaskComplete(taskId: Long)

    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND (:searchQuery IS NULL OR title LIKE '%' || :searchQuery || '%')
        AND (:category IS NULL OR category = :category)
        AND (:priority IS NULL OR priority = :priority)
        AND (:isCompleted IS NULL OR isCompleted = :isCompleted)
        ORDER BY createdAt DESC
    """)
    fun getFilteredTasks(
        userId: String,
        searchQuery: String?,
        category: String?,
        priority: String?,
        isCompleted: Boolean?
    ): Flow<List<TaskEntity>>
}

