package com.example.todolist.data.local.database.entity

import androidx.room.Entity

@Entity(
    primaryKeys = ["taskId", "tagId"],
    tableName = "task_tag_cross_ref"
)
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)