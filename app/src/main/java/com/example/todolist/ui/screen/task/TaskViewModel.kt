package com.example.todolist.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.Tag
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskCategory
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import com.example.todolist.domain.usecase.tag.AddTagToTaskUseCase
import com.example.todolist.domain.usecase.tag.GetTagsUseCase
import com.example.todolist.domain.usecase.tag.GetTaskWithTagsUseCase
import com.example.todolist.domain.usecase.tag.RemoveTagFromTaskUseCase
import com.example.todolist.domain.usecase.task.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.todolist.domain.model.TaskFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompleteUseCase: ToggleTaskCompleteUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,

    private val getTagsUseCase: GetTagsUseCase,
    private val getTaskWithTagsUseCase: GetTaskWithTagsUseCase,
    private val addTagToTaskUseCase: AddTagToTaskUseCase,
    private val removeTagFromTaskUseCase: RemoveTagFromTaskUseCase

) : ViewModel() {

    private val _filterState = MutableStateFlow(TaskFilter())
    val filterState = _filterState.asStateFlow()

    private val _taskUiState = MutableStateFlow<TaskUiState>(TaskUiState.Idle)
    val taskUiState: StateFlow<TaskUiState> = _taskUiState.asStateFlow()

    private val _taskOperationState = MutableStateFlow<TaskOperationState>(TaskOperationState.Idle)
    val taskOperationState: StateFlow<TaskOperationState> = _taskOperationState.asStateFlow()

    private val currentUserId = MutableStateFlow<String?>(null)

    private val _selectedTagIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTagIds: StateFlow<Set<Long>> = _selectedTagIds.asStateFlow()

    // Lấy danh sách tất cả các thẻ để hiển thị
    val allTags: StateFlow<List<Tag>> = getTagsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private var initialTagIds: Set<Long> = emptySet()

    init {
        loadCurrentUser()
        observeTasks()
    }

    private fun loadCurrentUser() {
        val user = getCurrentUserUseCase()
        currentUserId.value = user?.uid
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTasks() {
        viewModelScope.launch {
            // Sử dụng combine để tự động tải lại danh sách khi UserId HOẶC Bộ lọc thay đổi
            combine(
                currentUserId.filterNotNull(),
                _filterState
            ) { userId, filter ->
                Pair(userId, filter)
            }.flatMapLatest { (userId, filter) ->
                getTasksUseCase(userId, filter) // Gọi UseCase với bộ lọc
            }.collect { tasks ->
                _taskUiState.value = TaskUiState.Success(tasks)
            }
        }
    }

    fun addTask(task: Task) {
        val userId = currentUserId.value
        if (userId == null) {
            _taskOperationState.value = TaskOperationState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val taskWithUserId = task.copy(userId = userId)
                val taskId = addTaskUseCase(taskWithUserId)
                updateTags(taskId)
                _taskOperationState.value = TaskOperationState.Success
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Failed to add task")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                updateTaskUseCase(task)
                updateTags(task.id)
                _taskOperationState.value = TaskOperationState.Success
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Failed to update task")
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                deleteTaskUseCase(taskId)
                _taskOperationState.value = TaskOperationState.Success
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Failed to delete task")
            }
        }
    }

    fun toggleTaskComplete(taskId: Long) {
        viewModelScope.launch {
            try {
                toggleTaskCompleteUseCase(taskId)
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Failed to toggle task")
            }
        }
    }

    fun resetOperationState() {
        _taskOperationState.value = TaskOperationState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
            } finally {
                currentUserId.value = null
                _taskUiState.value = TaskUiState.Idle
                _taskOperationState.value = TaskOperationState.Idle
            }
        }
    }
    fun toggleTagSelection(tagId: Long) {
        _selectedTagIds.update { currentSet ->
            if (currentSet.contains(tagId)) {
                // chon roi thi bo chon
                currentSet - tagId
            } else {
                if (currentSet.size < 3) {
                    // chưa đu
                    currentSet + tagId
                } else {
                    currentSet
                }
            }
        }
    }

    suspend fun updateTags(taskId: Long) {
        val newIds = _selectedTagIds.value
        val oldIds = initialTagIds

        //Tìm những nhãn mới được thêm vào
        val tagsToAdd = newIds.minus(oldIds)
        tagsToAdd.forEach { tagId ->
            addTagToTaskUseCase(taskId, tagId)
        }

        //Tìm những nhãn đã bị bỏ chọn để xóa đi
        val tagsToRemove = oldIds.minus(newIds)
        tagsToRemove.forEach { tagId ->
            removeTagFromTaskUseCase(taskId, tagId)
        }
        initialTagIds = newIds
    }

    fun loadTaskDetails(taskId: Long) {
        viewModelScope.launch {
            // 1. Lấy dữ liệu từ Database 📂
            val taskWithTags = getTaskWithTagsUseCase(taskId).first()

            // 2. Trích xuất danh sách ID bằng hàm map (như bạn đã nói ở trên) 🗺️
            val ids = taskWithTags.tags.map { it.id }.toSet()

            // 3. Đưa vào các danh sách để quản lý
            _selectedTagIds.value = ids
            initialTagIds = ids
        }
    }

    fun prepareForNewTask() {
        _selectedTagIds.value = emptySet()
        initialTagIds = emptySet()
    }

    fun getTagsForTask(taskId: Long): Flow<List<Tag>> {
        return getTaskWithTagsUseCase(taskId).map { it.tags }
    }
    // --- CÁC HÀM XỬ LÝ SỰ KIỆN LỌC ---

    fun onSearchQueryChanged(newQuery: String) {
        _filterState.value = _filterState.value.copy(searchQuery = newQuery.ifBlank { null })
    }

    fun onCategorySelected(category: TaskCategory?) {
        // Nếu chọn lại category đang chọn thì bỏ lọc (null)
        val nextCategory = if (_filterState.value.category == category) null else category
        _filterState.value = _filterState.value.copy(category = nextCategory)
    }

    fun onPrioritySelected(priority: PriorityLevel?) {
        val nextPriority = if (_filterState.value.priority == priority) null else priority
        _filterState.value = _filterState.value.copy(priority = nextPriority)
    }

    fun onTagSelected(tagId: Long?) {
        val nextTagId = if (_filterState.value.tagId == tagId) null else tagId
        _filterState.value = _filterState.value.copy(tagId = nextTagId)
    }

    fun onStatusFilterSelected(isCompleted: Boolean?) {
        // Nếu chọn lại cái đang được chọn thì trả về null (Tất cả)
        val nextStatus = if (_filterState.value.isCompleted == isCompleted) null else isCompleted
        _filterState.value = _filterState.value.copy(isCompleted = nextStatus)
    }
}


