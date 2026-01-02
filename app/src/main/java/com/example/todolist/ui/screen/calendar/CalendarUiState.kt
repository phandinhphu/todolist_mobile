package com.example.todolist.ui.screen.calendar

import android.annotation.SuppressLint
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import java.time.LocalDate
import java.time.YearMonth

@SuppressLint("NewApi")
data class CalendarUiState(
        val currentMonth: YearMonth = YearMonth.now(),
        val selectedDate: LocalDate = LocalDate.now(),
        val dates: List<CalendarDay> = emptyList(),
        val tasks: List<Task> = emptyList(),
        val isLoading: Boolean = false
)

data class CalendarDay(
        val date: LocalDate,
        val isCurrentMonth: Boolean,
        val isToday: Boolean,
        val priorities: List<PriorityLevel> = emptyList()
)
