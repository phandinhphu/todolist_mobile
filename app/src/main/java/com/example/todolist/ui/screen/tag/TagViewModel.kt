package com.example.todolist.ui.screen.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.usecase.tag.AddTagUseCase
import com.example.todolist.domain.usecase.tag.DeleteTagUseCase
import com.example.todolist.domain.usecase.tag.GetTagsUseCase
import com.example.todolist.domain.usecase.tag.UpdateTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
    private val addTagUseCase: AddTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
): ViewModel() {
    // 1. Quản lý trạng thái UI
    private val _uiState = MutableStateFlow(TagUiState())
    val uiState: StateFlow<TagUiState> = _uiState.asStateFlow()

    init {
        // 2. Tự động theo dõi danh sách thẻ từ Database
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTagsUseCase().collect { tags ->
                _uiState.update { it.copy(tags = tags, isLoading = false) }
            }
        }
    }

    // 3. Xử lý các sự kiện (Events)
    fun onAddClick() {
        _uiState.update { it.copy(isAddDialogOpen = true, selectedTag = null) }
    }

    fun onEditClick(tag: Tag) {
        _uiState.update { it.copy(isAddDialogOpen = true, selectedTag = tag) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(isAddDialogOpen = false, selectedTag = null) }
    }

    fun onSaveTag(name: String, color: String) {
        viewModelScope.launch {
            val currentTag = _uiState.value.selectedTag
            if (currentTag == null) {
                // Thêm mới
                addTagUseCase(Tag(name = name, color = color))
            } else {
                // Cập nhật thẻ hiện tại
                updateTagUseCase(currentTag.copy(name = name, color = color))
            }
            onDismissDialog()
        }
    }

    fun onDeleteTag(tagId: Long) {
        viewModelScope.launch {
            deleteTagUseCase(tagId) //
        }
    }
}