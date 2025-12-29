package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.model.TaskWithTags
import com.example.todolist.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskWithTagsUseCase @Inject constructor(
    private val repository: TagRepository
) {
    operator fun invoke(taskId: Long): Flow<TaskWithTags> {
        return repository.getTaskWithTags(taskId)
    }
}