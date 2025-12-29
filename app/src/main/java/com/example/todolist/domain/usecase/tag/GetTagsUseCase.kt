package com.example.todolist.domain.usecase.tag

import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(
    private val repository: TagRepository
) {
    // Vì repository.getAllTags() trả về Flow nên chúng ta không cần 'suspend' ở đây
    operator fun invoke(): Flow<List<Tag>> {
        return repository.getAllTags()
    }
}