package com.example.todolist.ui.screen.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.Tag
import androidx.core.graphics.toColorInt

@Composable
fun AddTagDialog(
    selectedTag: Tag?,
    onDismiss: () -> Unit,
    onSave: (name: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(selectedTag?.name ?: "") }
    var color by remember { mutableStateOf(selectedTag?.color ?: "#6200EE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (selectedTag == null) "Add new tag" else "Edit tags")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tag name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Choose color:", style = MaterialTheme.typography.bodyMedium)

                // Gọi component chọn màu
                ColorPickerGrid(
                    selectedColor = color,
                    onColorChange = { newColor ->
                        color = newColor
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, color) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorPickerGrid(
    selectedColor: String,
    onColorChange: (String) -> Unit
) {
    val colors = listOf(
        "#2196F3", "#F44336", "#4CAF50", "#FFEB3B", "#FF9800",
        "#9C27B0", "#E91E63", "#009688", "#9E9E9E", "#212121"
    )

    // Chia danh sách 10 màu thành các hàng, mỗi hàng 5 màu
    val colorRows = colors.chunked(5)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        colorRows.forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowColors.forEach { colorCode ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(colorCode.toColorInt()),
                                shape = CircleShape
                            )
                            .border(
                                width = if (colorCode == selectedColor) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(colorCode) }
                    )
                }
            }
        }
    }
}