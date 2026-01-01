package com.example.todolist.data.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todolist.data.broadcast.ReminderReceiver
import com.example.todolist.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val MINUTE_MS = 60 * 1000L
        const val EARLY_WARNING_OFFSET = 30 * MINUTE_MS // 30 minutes
    }

    fun scheduleReminder(task: Task) {
        val reminderTime = task.reminderTime ?: return
        if (reminderTime <= System.currentTimeMillis()) return

        // 1. Schedule Early Warning (if applicable)
        val earlyTime = reminderTime - EARLY_WARNING_OFFSET
        if (earlyTime > System.currentTimeMillis()) {
            scheduleAlarm(
                    taskId = task.id,
                    time = earlyTime,
                    type = ReminderReceiver.TYPE_EARLY_WARNING,
                    title = task.title,
                    desc = task.description,
                    category = task.category.name,
                    priority = task.priority.name
            )
        }

        // 2. Schedule Exact Alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // ideally, handle permission request in UI
                return
            }
        }

        scheduleAlarm(
                taskId = task.id,
                time = reminderTime,
                type = ReminderReceiver.TYPE_ALARM,
                title = task.title,
                desc = task.description,
                category = task.category.name,
                priority = task.priority.name
        )
    }

    fun cancelReminder(taskId: Long) {
        cancelAlarm(taskId, ReminderReceiver.TYPE_EARLY_WARNING)
        cancelAlarm(taskId, ReminderReceiver.TYPE_ALARM)
    }

    private fun scheduleAlarm(
            taskId: Long,
            time: Long,
            type: Int,
            title: String,
            desc: String?,
            category: String?,
            priority: String?
    ) {
        val intent =
                Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
                    putExtra(ReminderReceiver.EXTRA_TYPE, type) // Distinguish type
                    putExtra(ReminderReceiver.EXTRA_TASK_TITLE, title)
                    putExtra(ReminderReceiver.EXTRA_TASK_DESC, desc)
                    putExtra(ReminderReceiver.EXTRA_TASK_CATEGORY, category)
                    putExtra(ReminderReceiver.EXTRA_TASK_PRIORITY, priority)
                }

        val requestCode = getRequestCode(taskId, type)
        val pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun cancelAlarm(taskId: Long, type: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        getRequestCode(taskId, type),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        alarmManager.cancel(pendingIntent)
    }

    private fun getRequestCode(taskId: Long, type: Int): Int {
        return (taskId * 2 + type).toInt()
    }
}
