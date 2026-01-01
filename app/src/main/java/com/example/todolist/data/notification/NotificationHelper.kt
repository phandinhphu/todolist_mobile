package com.example.todolist.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.broadcast.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {
        companion object {
                const val CHANNEL_EARLY_WARNING_ID = "todo_early_warning_channel"
                const val CHANNEL_ALARM_ID = "todo_alarm_channel"
        }

        private val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        init {
                createChannels()
        }

        private fun createChannels() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val earlyChannel =
                                NotificationChannel(
                                                CHANNEL_EARLY_WARNING_ID,
                                                "Early Warnings",
                                                NotificationManager.IMPORTANCE_DEFAULT
                                        )
                                        .apply { description = "Notifications for upcoming tasks" }

                        val alarmChannel =
                                NotificationChannel(
                                                CHANNEL_ALARM_ID,
                                                "Alarms",
                                                NotificationManager
                                                        .IMPORTANCE_HIGH // Critical for Heads-up
                                        )
                                        .apply {
                                                description = "Critical alarms for tasks"
                                                enableLights(true)
                                                enableVibration(true)
                                                vibrationPattern = longArrayOf(0, 500, 500, 500)
                                                lockscreenVisibility =
                                                        Notification.VISIBILITY_PUBLIC
                                                setSound(null, null)
                                        }

                        notificationManager.createNotificationChannel(earlyChannel)
                        notificationManager.createNotificationChannel(alarmChannel)
                }
        }

        fun buildEarlyWarningNotification(
                taskId: Long,
                title: String,
                desc: String?
        ): Notification {
                val activityIntent =
                        Intent(context, MainActivity::class.java).apply {
                                flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
                        }

                val pendingIntent =
                        PendingIntent.getActivity(
                                context,
                                taskId.toInt() * 10, // Avoid conflict
                                activityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                return NotificationCompat.Builder(context, CHANNEL_EARLY_WARNING_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Upcoming: $title")
                        .setContentText(desc ?: "You have a task coming up in 1 hour.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()
        }

        // Not used for full-screen alarm activity directly, but can be a fallback
        fun buildAlarmNotification(
                taskId: Long,
                title: String,
                desc: String?,
                category: String?,
                priority: String?
        ): Notification {
                // Intent for Full Screen Activity (AlarmActivity)
                val fullScreenIntent =
                        Intent(
                                        context,
                                        com.example.todolist.ui.screen.alarm.AlarmActivity::class
                                                .java
                                )
                                .apply {
                                        flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                                        Intent.FLAG_ACTIVITY_NO_USER_ACTION
                                        putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
                                        putExtra(ReminderReceiver.EXTRA_TASK_TITLE, title)
                                        putExtra(ReminderReceiver.EXTRA_TASK_DESC, desc)
                                        putExtra(ReminderReceiver.EXTRA_TASK_CATEGORY, category)
                                        putExtra(ReminderReceiver.EXTRA_TASK_PRIORITY, priority)
                                }

                val fullScreenPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                taskId.toInt() * 10 + 1,
                                fullScreenIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                // Intent for "View Details" action (MainActivity)
                val viewDetailsIntent =
                        Intent(context, MainActivity::class.java).apply {
                                flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
                        }

                val viewDetailsPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                taskId.toInt() * 10 + 2, // Different Request Code
                                viewDetailsIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                // Intent for "Dismiss" action (Service Stop)
                val dismissIntent =
                        Intent(context, com.example.todolist.service.AlarmService::class.java)
                                .apply {
                                        action =
                                                com.example.todolist.service.AlarmService
                                                        .ACTION_STOP
                                }
                val dismissPendingIntent =
                        PendingIntent.getService(
                                context,
                                taskId.toInt() * 10 + 3,
                                dismissIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                val largeIcon =
                        android.graphics.BitmapFactory.decodeResource(
                                context.resources,
                                R.mipmap.ic_launcher_round
                        )

                val keyguardManager =
                        context.getSystemService(Context.KEYGUARD_SERVICE) as
                                android.app.KeyguardManager

                val builder =
                        NotificationCompat.Builder(context, CHANNEL_ALARM_ID)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setLargeIcon(largeIcon)
                                .setContentTitle("ALARM: $title")
                                .setContentText(desc)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(fullScreenPendingIntent)
                                .setOngoing(true)
                                .setAutoCancel(false)
                                .setColor(
                                        androidx.core.content.ContextCompat.getColor(
                                                context,
                                                R.color.purple_500
                                        )
                                )
                                .setVibrate(longArrayOf(0L, 500L, 500L, 500L))
                                .addAction(
                                        android.R.drawable.ic_menu_view,
                                        "View Details",
                                        viewDetailsPendingIntent
                                )
                                .addAction(
                                        android.R.drawable.ic_menu_close_clear_cancel,
                                        "Dismiss",
                                        dismissPendingIntent
                                )
                                .setDeleteIntent(dismissPendingIntent)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setFullScreenIntent(fullScreenPendingIntent, true)

                val notification = builder.build()
                notification.flags = notification.flags or Notification.FLAG_INSISTENT
                return notification
        }

        fun notify(id: Int, notification: Notification) {
                notificationManager.notify(id, notification)
        }
}
