package com.example.todolist.domain.model

data class HomeStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val remainingTasks: Int,
    val progressPercentage: Int,
    val upcomingTasks: List<Task>,
    val allTasks: List<Task>
)
