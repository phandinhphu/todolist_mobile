package com.example.todolist.domain.model

data class TaskFilter(
    val searchQuery: String? = null,
    val category: TaskCategory? = null,
    val priority: PriorityLevel? = null,
    val tagId: Long? = null,
    val isCompleted: Boolean? = null
)