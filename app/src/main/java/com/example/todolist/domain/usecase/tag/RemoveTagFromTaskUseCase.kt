package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.repository.TagRepository
import javax.inject.Inject

class RemoveTagFromTaskUseCase @Inject constructor(
    private  val repository: TagRepository
) {
    suspend operator fun invoke(taskId: Long, tagId: Long): Int {
        return repository.removeTagFromTask(taskId, tagId)
    }
}