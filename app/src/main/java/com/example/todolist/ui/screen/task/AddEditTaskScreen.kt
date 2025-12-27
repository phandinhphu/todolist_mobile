package com.example.todolist.ui.screen.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskCategory
import com.example.todolist.route.Routes
import com.example.todolist.util.DateFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavHostController,
    taskId: Long? = null,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val taskOperationState by viewModel.taskOperationState.collectAsState()
    val taskUiState by viewModel.taskUiState.collectAsState()
    
    // State variables
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.PERSONAL) }
    var selectedPriority by remember { mutableStateOf(PriorityLevel.MEDIUM) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var hasLoadedTask by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Reset state khi taskId thay đổi
    LaunchedEffect(taskId) {
        if (taskId == null) {
            // Add mode - reset về giá trị mặc định
            title = ""
            description = ""
            selectedCategory = TaskCategory.PERSONAL
            selectedPriority = PriorityLevel.MEDIUM
            dueDate = null
            reminderTime = null
            hasLoadedTask = false
        } else {
            // Edit mode - reset flag để load lại
            hasLoadedTask = false
            title = ""
            description = ""
            dueDate = null
            reminderTime = null
        }
    }

    // Load task khi taskUiState có dữ liệu và chưa load
    LaunchedEffect(taskId, taskUiState) {
        if (taskId != null && !hasLoadedTask) {
            try {
                when (val state = taskUiState) {
                    is TaskUiState.Success -> {
                        val task = state.tasks.find { it.id == taskId }
                        if (task != null) {
                            title = task.title
                            description = task.description ?: ""
                            selectedCategory = task.category
                            selectedPriority = task.priority
                            dueDate = task.dueDate
                            reminderTime = task.reminderTime
                            hasLoadedTask = true
                        } else {
                            // Task không tìm thấy - có thể đã bị xóa
                            // Quay lại màn hình trước
                            navController.popBackStack()
                        }
                    }
                    is TaskUiState.Loading -> {
                        // Đang load, đợi
                    }
                    else -> {
                        // Chưa có dữ liệu, sẽ load khi state thành Success
                    }
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu có
                e.printStackTrace()
                navController.popBackStack()
            }
        }
    }

    // Handle successful save
    LaunchedEffect(taskOperationState) {
        if (taskOperationState is TaskOperationState.Success) {
            navController.popBackStack()
            viewModel.resetOperationState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Add Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        // Show loading nếu đang edit nhưng chưa load được task
        if (taskId != null && !hasLoadedTask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Field
                OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Priority Selection
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityLevel.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority.name) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (priority) {
                                PriorityLevel.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                PriorityLevel.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                PriorityLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    )
                }
            }

            // Due Date Selection
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.titleSmall
            )
            OutlinedButton(
                onClick = {
                    val datePicker = DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(year, month, dayOfMonth)
                            dueDate = selectedCalendar.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePicker.show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (dueDate != null) {
                        DateFormatter.formatDate(dueDate!!)
                    } else {
                        "Select Due Date"
                    }
                )
                if (dueDate != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { dueDate = null },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text("Clear", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Reminder Time Selection
            Text(
                text = "Reminder Time",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val baseCalendar = Calendar.getInstance()
                        // Nếu có dueDate, sử dụng ngày của dueDate
                        if (dueDate != null) {
                            baseCalendar.timeInMillis = dueDate!!
                        }
                        
                        val currentHour = baseCalendar.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = baseCalendar.get(Calendar.MINUTE)
                        
                        val timePicker = TimePickerDialog(
                            context,
                            { _: TimePicker, hourOfDay: Int, minute: Int ->
                                val selectedCalendar = Calendar.getInstance()
                                // Nếu có dueDate, sử dụng ngày của dueDate
                                if (dueDate != null) {
                                    selectedCalendar.timeInMillis = dueDate!!
                                }
                                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                selectedCalendar.set(Calendar.MINUTE, minute)
                                selectedCalendar.set(Calendar.SECOND, 0)
                                reminderTime = selectedCalendar.timeInMillis
                            },
                            currentHour,
                            currentMinute,
                            true // 24 hour format
                        )
                        timePicker.show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Select Time",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (reminderTime != null) {
                            DateFormatter.formatDateTime(reminderTime!!)
                        } else {
                            "Set Reminder"
                        }
                    )
                }
                if (reminderTime != null) {
                    TextButton(
                        onClick = { reminderTime = null }
                    ) {
                        Text("Clear")
                    }
                }
            }

            // Error Message
            if (taskOperationState is TaskOperationState.Error) {
                Text(
                    text = (taskOperationState as TaskOperationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = if (taskId != null && taskUiState is TaskUiState.Success) {
                            val existingTask = (taskUiState as TaskUiState.Success).tasks.find { it.id == taskId }
                            existingTask?.copy(
                                title = title,
                                description = description.ifBlank { null },
                                category = selectedCategory,
                                priority = selectedPriority,
                                dueDate = dueDate,
                                reminderTime = reminderTime
                            ) ?: return@Button
                        } else {
                            Task(
                                id = 0, // Will be auto-generated
                                title = title,
                                description = description.ifBlank { null },
                                category = selectedCategory,
                                priority = selectedPriority,
                                dueDate = dueDate,
                                reminderTime = reminderTime,
                                userId = "" // Will be set by ViewModel
                            )
                        }

                        if (taskId == null) {
                            viewModel.addTask(task)
                        } else {
                            viewModel.updateTask(task)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && taskOperationState !is TaskOperationState.Loading
            ) {
                if (taskOperationState is TaskOperationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (taskId == null) "Add Task" else "Update Task")
            }
            }
        }
    }
}
