package com.example.todolist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
abstract class AppDatabase : RoomDatabase() {
}