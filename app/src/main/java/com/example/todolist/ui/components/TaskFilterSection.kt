package com.example.todolist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.TaskCategory
import com.example.todolist.domain.model.TaskFilter
import com.example.todolist.domain.model.Tag
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterSection(
    filter: TaskFilter,
    allTags: List<Tag>,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (TaskCategory?) -> Unit,
    onPriorityChange: (PriorityLevel?) -> Unit,
    onTagChange: (Long?) -> Unit,
    onStatusChange: (Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Thanh tìm kiếm (Search Bar)
        OutlinedTextField(
            value = filter.searchQuery ?: "",
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (!filter.searchQuery.isNullOrEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Hàng lọc Category & Priority
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Lọc theo Category
            items(TaskCategory.entries) { category ->
                FilterChip(
                    selected = filter.category == category,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    leadingIcon = if (filter.category == category) {
                        { Icon(Icons.AutoMirrored.Filled.List, modifier = Modifier.size(16.dp), contentDescription = null) }
                    } else null
                )
            }

            // Đường kẻ chia cách nhẹ
            item { VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)) }

            // Lọc theo Priority
            items(PriorityLevel.entries) { priority ->
                FilterChip(
                    selected = filter.priority == priority,
                    onClick = { onPriorityChange(priority) },
                    label = { Text(priority.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            // Đường kẻ chia cách nhẹ
            item { VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)) }

            // Lọc theo tag
            items(allTags) { tag ->
                FilterChip(
                    selected = filter.tagId == tag.id,
                    onClick = { onTagChange(tag.id) },
                    label = { Text("#${tag.name}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            }

            // Đường kẻ chia cách nhẹ
            item { VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp)) }

            // Lọc theo trạng thái hoàn thành (IsCompleted)
            item {
                FilterChip(
                    selected = filter.isCompleted == false,
                    onClick = { onStatusChange(false) },
                    label = { Text("Doing") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            item {
                FilterChip(
                    selected = filter.isCompleted == true,
                    onClick = { onStatusChange(true) },
                    label = { Text("Done") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

        }
    }
}