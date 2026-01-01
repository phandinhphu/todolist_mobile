package com.example.todolist.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.data.manager.ReminderManager
import com.example.todolist.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var reminderManager: ReminderManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                // Fetch all tasks with future reminders
                val tasks = taskRepository.getAllReminders()
                
                tasks.forEach { task ->
                    reminderManager.scheduleReminder(task)
                }
            }
        }
    }
}
