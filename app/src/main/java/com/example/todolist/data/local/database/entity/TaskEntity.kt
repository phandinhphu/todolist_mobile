package com.example.todolist.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.TaskCategory

@Entity(
    tableName = "tasks",
    indices = [Index("userId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String?,

    // Phân loại: Cá nhân / Công việc / Học tập
    val category: TaskCategory,

    // Độ ưu tiên
    val priority: PriorityLevel,

    // Trạng thái hoàn thành
    val isCompleted: Boolean = false,

    // Thời gian nhắc nhở (nullable)
    val reminderTime: Long?,

    // Deadline (nullable)
    val dueDate: Long?,

    // Gắn với user Firebase
    val userId: String,

    // Thời gian tạo
    val createdAt: Long = System.currentTimeMillis()
)