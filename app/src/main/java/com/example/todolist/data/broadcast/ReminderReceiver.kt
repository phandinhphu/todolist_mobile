package com.example.todolist.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.data.notification.NotificationHelper
import com.example.todolist.ui.screen.alarm.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_DESC = "extra_task_desc"
        const val EXTRA_TASK_CATEGORY = "extra_task_category"
        const val EXTRA_TASK_PRIORITY = "extra_task_priority"
        const val EXTRA_TYPE = "extra_type"

        const val TYPE_EARLY_WARNING = 0
        const val TYPE_ALARM = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val type = intent.getIntExtra(EXTRA_TYPE, TYPE_EARLY_WARNING)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val desc = intent.getStringExtra(EXTRA_TASK_DESC)
        val category = intent.getStringExtra(EXTRA_TASK_CATEGORY)
        val priority = intent.getStringExtra(EXTRA_TASK_PRIORITY)

        if (taskId == -1L) return

        if (type == TYPE_EARLY_WARNING) {
            val notification = notificationHelper.buildEarlyWarningNotification(taskId, title, desc)
            notificationHelper.notify(taskId.toInt() * 10, notification)
        } else {
            // TYPE_ALARM
            val activityIntent =
                    Intent(context, AlarmActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra(EXTRA_TASK_ID, taskId)
                        putExtra(EXTRA_TASK_TITLE, title)
                        putExtra(EXTRA_TASK_DESC, desc)
                        putExtra(EXTRA_TASK_CATEGORY, category)
                        putExtra(EXTRA_TASK_PRIORITY, priority)
                    }
            context.startActivity(activityIntent)

            // Optionally also show a high priority notification if screen is unlocked and activity
            // doesn't pop
            val alarmNotification = notificationHelper.buildAlarmNotification(taskId, title, desc)
            notificationHelper.notify(taskId.toInt() * 10 + 1, alarmNotification)
        }
    }
}
