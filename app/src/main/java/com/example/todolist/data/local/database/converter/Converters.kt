package com.example.todolist.data.local.database.converter

import androidx.room.TypeConverter
import com.example.todolist.domain.model.PriorityLevel
import com.example.todolist.domain.model.TaskCategory

class Converters {
    @TypeConverter
    fun fromCategory(value: TaskCategory) = value.name

    @TypeConverter
    fun toCategory(value: String) = TaskCategory.valueOf(value)

    @TypeConverter
    fun fromPriority(value: PriorityLevel) = value.name

    @TypeConverter
    fun toPriority(value: String) = PriorityLevel.valueOf(value)
}