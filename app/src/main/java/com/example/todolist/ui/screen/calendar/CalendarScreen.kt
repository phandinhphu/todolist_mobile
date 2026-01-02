package com.example.todolist.ui.screen.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.todolist.ui.screen.task.TaskItem
import com.example.todolist.ui.screen.task.TaskViewModel

@SuppressLint("NewApi")
@Composable
fun CalendarScreen(
        viewModel: CalendarViewModel = hiltViewModel(),
        taskViewModel: TaskViewModel = hiltViewModel(),
        onTaskClick: (Task) -> Unit = {},
        onDateDoubleClick: (LocalDate) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var totalDragAmount = 0f

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            CalendarHeader(
                    currentMonth =
                            uiState.currentMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("vi-VN"))
                            )
                            .replaceFirstChar { it.uppercase() },
                    onPreviousMonth = viewModel::onPreviousMonth,
                    onNextMonth = viewModel::onNextMonth
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            CalendarGrid(
                    dates = uiState.dates,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = viewModel::onDateSelected,
                    onDateDoubleClick = onDateDoubleClick,
                    modifier = Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                totalDragAmount = 0f
                            },
                            onDragEnd = {
                                val threshold = 200f
                                if (totalDragAmount < -threshold) {
                                    viewModel.onNextMonth()
                                } else if (totalDragAmount > threshold) {
                                    viewModel.onPreviousMonth()
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDragAmount += dragAmount
                            }
                        )
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                    text = "Danh sách công việc",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            if (uiState.tasks.isEmpty()) {
                item {
                    Text(
                            text = "Không có công việc nào",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(uiState.tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { onTaskClick(task) },
                        onToggleComplete = { taskViewModel.toggleTaskComplete(task.id) },
                        onDelete = { taskViewModel.deleteTask(task.id) },
                        viewModel = taskViewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(currentMonth: String, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Month")
        }

        Text(
                text = currentMonth,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarGrid(
        dates: List<CalendarDay>,
        selectedDate: LocalDate,
        onDateSelected: (LocalDate) -> Unit,
        onDateDoubleClick: (LocalDate) -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Weekday Headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            daysOfWeek.forEach { day ->
                Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid Content using Column and Rows
        // Chunk dates into rows of 7
        val chunkedDates = dates.chunked(7)
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunkedDates.forEach { weekDates ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekDates.forEach { calendarDay ->
                        Box(modifier = Modifier.weight(1f)) {
                           DayItem(
                                   day = calendarDay,
                                   isSelected = calendarDay.date == selectedDate,
                                   onClick = { onDateSelected(calendarDay.date) },
                                   onDoubleClick = { onDateDoubleClick(calendarDay.date) }
                           )
                        }
                    }
                    // Fill remaining space if last week is incomplete (though our logic guarantees 42 items)
                    repeat(7 - weekDates.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("NewApi")
@Composable
fun DayItem(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit, onDoubleClick: () -> Unit) {
    val isToday = day.isToday

    // Background handling
    val backgroundColor =
            if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor =
            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else if (isToday) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
    val borderColor =
            if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
            modifier =
                    Modifier.aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else borderColor,
                                    RoundedCornerShape(8.dp)
                            )
                            .combinedClickable(
                                onClick = onClick,
                                onDoubleClick = onDoubleClick
                            )
                            .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (!day.isCurrentMonth) Color.LightGray else contentColor,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Priority Dots
        if (day.priorities.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
               // Show maximum 3 dots to avoid overflow
               day.priorities.take(3).forEach { priority ->
                    val dotColor = when (priority) {
                        PriorityLevel.HIGH -> Color.Red
                        PriorityLevel.MEDIUM -> Color.Yellow
                        PriorityLevel.LOW -> Color.Green
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
               }
            }
        } else {
            Box(modifier = Modifier.size(6.dp)) // Spacer to keep height consistent
        }
    }
}
