package com.example.todolist.data.local.database.dao

import androidx.room.*
import com.example.todolist.data.local.database.entity.TagEntity
import com.example.todolist.data.local.database.entity.TaskTagCrossRef
import com.example.todolist.data.local.database.entity.TaskWithTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: Long): Int

    @Update
    suspend fun updateTag(tag: TagEntity): Int

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef): Long

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: Long, tagId: Long): Int

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithTags(taskId: Long): Flow<TaskWithTagsEntity>
}