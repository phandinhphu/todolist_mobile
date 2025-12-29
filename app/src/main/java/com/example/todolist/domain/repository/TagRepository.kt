package com.example.todolist.domain.repository

import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.model.TaskWithTags
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun insertTag(tag: Tag): Long
    suspend fun updateTag(tag: Tag): Int
    suspend fun deleteTag(tagId: Long): Int
    suspend fun addTagToTask(taskId: Long, tagId: Long): Long
    suspend fun removeTagFromTask(taskId: Long, tagId: Long): Int
    fun getTaskWithTags(taskId: Long): Flow<TaskWithTags>
}