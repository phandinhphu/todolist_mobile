package com.example.todolist.ui.screen.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.model.TaskCategory
import com.example.todolist.ui.components.clearFocusOnTap
import com.example.todolist.util.DateFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    navController: NavHostController,
    taskId: Long? = null,
    initialDate: Long? = null,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val taskOperationState by viewModel.taskOperationState.collectAsState()
    val taskUiState by viewModel.taskUiState.collectAsState()
    
    // State variables
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.PERSONAL) }
    var selectedPriority by remember { mutableStateOf(PriorityLevel.MEDIUM) }
    var dueDate by remember { mutableStateOf<Long?>(initialDate) }
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
            dueDate = initialDate
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

    LaunchedEffect(taskId) {
        taskId?.let { id ->
            viewModel.loadTaskDetails(id)
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
                .clearFocusOnTap()
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

                    TagSelectionSection(viewModel)
                    // Category Selection Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ){
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
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
                                    SelectableChip(
                                        label = when (category) {
                                            TaskCategory.PERSONAL -> "👤 Personal"
                                            TaskCategory.WORK -> "💼 Work"
                                            TaskCategory.STUDY -> "📚 Study"
                                        },
                                        isSelected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        modifier = Modifier.weight(1f)
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
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
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
                                    SelectableChip(
                                        label = when (priority) {
                                            PriorityLevel.HIGH -> "🔴 High"
                                            PriorityLevel.MEDIUM -> "🟡 Medium"
                                            PriorityLevel.LOW -> "🟢 Low"
                                        },
                                        isSelected = selectedPriority == priority,
                                        onClick = { selectedPriority = priority },
                                        modifier = Modifier.weight(1f)
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagSelectionSection(viewModel: TaskViewModel) {
    val allTags by viewModel.allTags.collectAsState()
    val selectedIds by viewModel.selectedTagIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tiêu đề Section kèm Icon giống style My Tasks
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Label,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            // Hiển thị số lượng đã chọn (0/3)
            Text(
                text = "${selectedIds.size}/3",
                style = MaterialTheme.typography.labelMedium,
                color = if (selectedIds.size >= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Vùng chứa các Chip sử dụng FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allTags.forEach { tag ->
                val isSelected = selectedIds.contains(tag.id)

                // Sử dụng FilterChip với Style bo tròn 8dp giống Priority Chip của bạn
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleTagSelection(tag.id) },
                    label = {
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(8.dp), // Đồng bộ với Priority/Category Chip
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        // Khi chọn: dùng màu Primary Container
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        // Khi chưa chọn: dùng màu xám nhạt giống SurfaceVariant của bạn
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
@Composable
fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp), // Giảm nhẹ chiều cao để trông thanh thoát hơn
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp), // Padding nhỏ để chữ không chạm biên khi quá dài
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                // Sử dụng bodySmall hoặc labelSmall để cỡ chữ nhỏ hơn, dễ vừa khung hơn
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Clip, // Giữ nguyên để cố gắng hiển thị đủ
                softWrap = false
            )
        }
    }
}
