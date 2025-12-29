package com.example.todolist.data.repository

import com.example.todolist.domain.model.Tag
import com.example.todolist.data.local.database.dao.TagDao
import com.example.todolist.data.local.database.entity.TaskTagCrossRef
import com.example.todolist.domain.model.TaskWithTags
import com.example.todolist.data.mapper.toDomain
import com.example.todolist.data.mapper.toEntity
import com.example.todolist.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map{
            entities -> entities.map{ it.toDomain()}
        }
    }

    override suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag.toEntity())
    }

    override suspend fun updateTag(tag: Tag): Int {
        return tagDao.updateTag(tag.toEntity())
    }

    override suspend fun deleteTag(tagId: Long): Int {
        return tagDao.deleteTag(tagId)
    }

    override suspend fun addTagToTask(taskId: Long, tagId: Long): Long {
        val crossRef = TaskTagCrossRef(taskId = taskId, tagId = tagId)
        return tagDao.insertTaskTagCrossRef(crossRef)
    }

    override suspend fun removeTagFromTask(taskId: Long, tagId: Long): Int {
        return  tagDao.deleteTaskTagCrossRef(taskId, tagId)
    }

    override fun getTaskWithTags(taskId: Long): Flow<TaskWithTags> {
        return tagDao.getTaskWithTags(taskId).map { it.toDomain() }
    }
}