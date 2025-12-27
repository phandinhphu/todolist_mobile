package com.example.todolist.ui.screen.task

import com.example.todolist.domain.model.Task

sealed class TaskUiState {
    object Idle : TaskUiState()
    object Loading : TaskUiState()
    data class Success(val tasks: List<Task>) : TaskUiState()
    data class Error(val message: String) : TaskUiState()
}

sealed class TaskOperationState {
    object Idle : TaskOperationState()
    object Loading : TaskOperationState()
    object Success : TaskOperationState()
    data class Error(val message: String) : TaskOperationState()
}

