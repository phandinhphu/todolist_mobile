package com.example.todolist.ui.screen.calendar

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.usecase.task.GetTasksByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@HiltViewModel
class CalendarViewModel
@Inject
constructor(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val taskRepository: com.example.todolist.domain.repository.TaskRepository,
    private val authRepository: com.example.todolist.domain.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        generateCalendarDates(YearMonth.now())
        fetchTasksForDate(LocalDate.now())
        fetchTaskPrioritiesForMonth(YearMonth.now())
    }

    fun refresh() {
        fetchTasksForDate(_uiState.value.selectedDate)
        fetchTaskPrioritiesForMonth(_uiState.value.currentMonth)
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        fetchTasksForDate(date)
    }

    fun onPreviousMonth() {
        val prevMonth = _uiState.value.currentMonth.minusMonths(1)
        _uiState.update { it.copy(currentMonth = prevMonth) }
        generateCalendarDates(prevMonth)
        fetchTaskPrioritiesForMonth(prevMonth)
    }

    fun onNextMonth() {
        val nextMonth = _uiState.value.currentMonth.plusMonths(1)
        _uiState.update { it.copy(currentMonth = nextMonth) }
        generateCalendarDates(nextMonth)
        fetchTaskPrioritiesForMonth(nextMonth)
    }

    private fun fetchTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val tasks = getTasksByDateUseCase(date)
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun fetchTaskPrioritiesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val startOfMonth = yearMonth.atDay(1)
            // Get the first displayed day (might be in previous month)
            val firstDayOfGrid = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            // Get the last displayed day (42 days later)
            val lastDayOfGrid = firstDayOfGrid.plusDays(41)

            val startMillis = firstDayOfGrid.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = lastDayOfGrid.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            try {
                val tasks = taskRepository.getTasksByDueDate(user.uid, startMillis, endMillis)
                val prioritiesMap = tasks.groupBy { 
                    it.dueDate?.let { dueDateMillis ->
                        java.time.Instant.ofEpochMilli(dueDateMillis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                }

                _uiState.update { state ->
                    val updatedDates = state.dates.map { calendarDay ->
                        val tasksForDay = prioritiesMap[calendarDay.date] ?: emptyList()
                        // Distinct priorities, sorted (HIGH first if needed, though Set ensures uniqueness)
                        val priorities = tasksForDay.map { it.priority }.distinct().sortedBy { it.ordinal } 
                        calendarDay.copy(priorities = priorities)
                    }
                    state.copy(dates = updatedDates)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateCalendarDates(yearMonth: YearMonth) {
        val startOfMonth = yearMonth.atDay(1)
        
        // Find the start of the week (Monday) prior to the start of the month
        val firstDayOfGrid = startOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // We want 6 rows of 7 days = 42 days total
        val dates = mutableListOf<CalendarDay>()
        var currentDate = firstDayOfGrid

        repeat(42) {
            dates.add(
                    CalendarDay(
                            date = currentDate,
                            isCurrentMonth = currentDate.month == yearMonth.month,
                            isToday = currentDate == LocalDate.now()
                            )
            )
            currentDate = currentDate.plusDays(1)
        }

        _uiState.update { it.copy(dates = dates) }
    }
}
