package com.example.todolist.data.mapper

import com.example.todolist.data.local.database.entity.TaskEntity
import com.example.todolist.domain.model.Task

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        category = category,
        priority = priority,
        isCompleted = isCompleted,
        reminderTime = reminderTime,
        dueDate = dueDate,
        userId = userId,
        createdAt = createdAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        priority = priority,
        isCompleted = isCompleted,
        reminderTime = reminderTime,
        dueDate = dueDate,
        userId = userId,
        createdAt = createdAt
    )
}

