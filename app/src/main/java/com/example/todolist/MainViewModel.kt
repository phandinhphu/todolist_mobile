package com.example.todolist

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    // Dùng quản lý theme, user session, v.v.
    init {
        Log.d("MainViewModel", "init")
    }
}