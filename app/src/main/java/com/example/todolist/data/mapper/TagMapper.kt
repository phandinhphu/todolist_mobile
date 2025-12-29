package com.example.todolist.data.mapper

import com.example.todolist.data.local.database.entity.TagEntity
import com.example.todolist.data.local.database.entity.TaskWithTagsEntity
import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.model.TaskWithTags

fun TagEntity.toDomain(): Tag = Tag(
    id = id,
    name = name,
    color = color
)

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name,
    color = color
)

fun TaskWithTagsEntity.toDomain(): TaskWithTags = TaskWithTags(
    task = task.toDomain(),
    tags = tags.map { it.toDomain() }
)