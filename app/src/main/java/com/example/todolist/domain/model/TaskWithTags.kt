package com.example.todolist.domain.model

data class TaskWithTags(
    val task: Task,
    val tags: List<Tag>
)