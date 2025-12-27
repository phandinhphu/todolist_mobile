package com.example.todolist.ui.screen.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                title = { 
                    Text(
                        if (taskId == null) "Add Task" else "Edit Task",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // Show loading nếu đang edit nhưng chưa load được task
            if (taskId != null && !hasLoadedTask) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Title Field Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Task Title *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Enter task title...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Description Field Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Add details about your task...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Category Selection Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Category",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TaskCategory.entries.forEach { category ->
                                    FilterChip(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        label = {
                                            Text(
                                                when (category) {
                                                    TaskCategory.PERSONAL -> "👤 Personal"
                                                    TaskCategory.WORK -> "💼 Work"
                                                    TaskCategory.STUDY -> "📚 Study"
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Priority Selection Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Priority",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PriorityLevel.entries.forEach { priority ->
                                    FilterChip(
                                        selected = selectedPriority == priority,
                                        onClick = { selectedPriority = priority },
                                        label = {
                                            Text(
                                                when (priority) {
                                                    PriorityLevel.HIGH -> "🔴 High"
                                                    PriorityLevel.MEDIUM -> "🟡 Medium"
                                                    PriorityLevel.LOW -> "🟢 Low"
                                                }
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Due Date Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Due Date",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
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
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
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
                            }
                            if (dueDate != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { dueDate = null },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Clear Date")
                                }
                            }
                        }
                    }

                    // Reminder Time Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Reminder",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    val baseCalendar = Calendar.getInstance()
                                    if (dueDate != null) {
                                        baseCalendar.timeInMillis = dueDate!!
                                    }
                                    
                                    val currentHour = baseCalendar.get(Calendar.HOUR_OF_DAY)
                                    val currentMinute = baseCalendar.get(Calendar.MINUTE)
                                    
                                    val timePicker = TimePickerDialog(
                                        context,
                                        { _: TimePicker, hourOfDay: Int, minute: Int ->
                                            val selectedCalendar = Calendar.getInstance()
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
                                        true
                                    )
                                    timePicker.show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
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
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { reminderTime = null },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Clear Reminder")
                                }
                            }
                        }
                    }

                    // Error Message
                    AnimatedVisibility(visible = taskOperationState is TaskOperationState.Error) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = (taskOperationState as? TaskOperationState.Error)?.message ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

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
                                        id = 0,
                                        title = title,
                                        description = description.ifBlank { null },
                                        category = selectedCategory,
                                        priority = selectedPriority,
                                        dueDate = dueDate,
                                        reminderTime = reminderTime,
                                        userId = ""
                                    )
                                }

                                if (taskId == null) {
                                    viewModel.addTask(task)
                                } else {
                                    viewModel.updateTask(task)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = title.isNotBlank() && taskOperationState !is TaskOperationState.Loading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (taskOperationState is TaskOperationState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (taskId == null) "Add Task" else "Update Task",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
