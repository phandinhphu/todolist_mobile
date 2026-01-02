package com.example.todolist.ui.root

import androidx.lifecycle.ViewModel
import com.example.todolist.domain.usecase.auth.GetCurrentUserUseCase
import com.example.todolist.route.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppAuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    val startDestination: String =
        if (getCurrentUserUseCase() != null) {
            Routes.Home.route
        } else {
            Routes.Login.route
        }
}