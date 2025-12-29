package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private  val repository: TagRepository
) {
    suspend operator fun invoke(tagId: Long): Int {
        return repository.deleteTag(tagId)
    }
}