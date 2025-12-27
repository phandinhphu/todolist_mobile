package com.example.todolist.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.domain.usecase.auth.LogoutUseCase
import com.example.todolist.domain.usecase.task.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            currentUserId
                .filterNotNull()
                .flatMapLatest { userId ->
                    getTasksUseCase(userId)
                }
                .collect { tasks ->
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
}


