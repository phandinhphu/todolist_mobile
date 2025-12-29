package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.repository.TagRepository
import javax.inject.Inject

class AddTagToTaskUseCase @Inject constructor(
    private  val repository: TagRepository
) {
    suspend operator fun invoke(taskId: Long, tagId: Long): Long {
       return repository.addTagToTask(taskId, tagId)
    }
}