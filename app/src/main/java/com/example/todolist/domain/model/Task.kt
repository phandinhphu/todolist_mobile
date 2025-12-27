package com.example.todolist.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val category: TaskCategory,
    val priority: PriorityLevel,
    val isCompleted: Boolean = false,
    val reminderTime: Long?,
    val dueDate: Long?,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis()
)

