package com.example.todolist.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskCategory
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import com.example.todolist.domain.usecase.task.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.todolist.domain.model.TaskFilter
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompleteUseCase: ToggleTaskCompleteUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _filterState = MutableStateFlow(TaskFilter())
    val filterState = _filterState.asStateFlow()

    private val _taskUiState = MutableStateFlow<TaskUiState>(TaskUiState.Idle)
    val taskUiState: StateFlow<TaskUiState> = _taskUiState.asStateFlow()

    private val _taskOperationState = MutableStateFlow<TaskOperationState>(TaskOperationState.Idle)
    val taskOperationState: StateFlow<TaskOperationState> = _taskOperationState.asStateFlow()

    private val currentUserId = MutableStateFlow<String?>(null)


    init {
        loadCurrentUser()
        observeTasks()
    }

    private fun loadCurrentUser() {
        val user = getCurrentUserUseCase()
        currentUserId.value = user?.uid
    }

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
                addTaskUseCase(taskWithUserId)
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

    fun clearAllFilters() {
        _filterState.value = TaskFilter()
    }
}


