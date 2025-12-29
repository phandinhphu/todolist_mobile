package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.repository.TagRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(
    private  val repository: TagRepository
) {
    suspend operator fun invoke(tag: Tag): Long {
        return repository.insertTag(tag)
    }
}