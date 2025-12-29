package com.example.todolist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todolist.data.local.database.converter.Converters
import com.example.todolist.data.local.database.dao.TagDao
import com.example.todolist.data.local.database.dao.TaskDao
import com.example.todolist.data.local.database.entity.*

@Database(
    entities = [
        TaskEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao
}
