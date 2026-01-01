package com.example.todolist.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.example.todolist.data.broadcast.ReminderReceiver
import com.example.todolist.data.manager.AndroidAudioPlayer
import com.example.todolist.data.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject lateinit var audioPlayer: AndroidAudioPlayer

    @Inject lateinit var notificationHelper: NotificationHelper

    companion object {
        const val ACTION_STOP = "ACTION_STOP"
        private const val AUTO_DISMISS_DELAY_MS = 30_000L
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val autoDismissRunnable = Runnable { stopSelf() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        val taskId = intent.getLongExtra(ReminderReceiver.EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_TITLE) ?: "Alarm"
        val desc = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_DESC)
        val category = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_CATEGORY)
        val priority = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_PRIORITY)

        // 1. Play Audio Immediately
        audioPlayer.play()

        // 2. Build Notification
        val notification =
                notificationHelper.buildAlarmNotification(taskId, title, desc, category, priority)

        // 3. Schedule Auto-Dismiss
        handler.removeCallbacks(autoDismissRunnable)
        handler.postDelayed(autoDismissRunnable, AUTO_DISMISS_DELAY_MS)

        // 3. Start Foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                    this,
                    taskId.toInt() * 10 + 1,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK // Or MEDIA_PLAYBACK if
                    // feasible, but usually
                    // SHORT_SERVICE or default is
                    // ok for Alarm?
                    // Actually for Alarm app, standard simpler types are often restricted.
                    // Let's use manifest type="shortService" if API 34+ or just default.
                    // For now, standard startForeground.
                    // NOTE: Android 14 requires specifying type in manifest.
                    )
        } else {
            startForeground(taskId.toInt() * 10 + 1, notification)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoDismissRunnable)
        audioPlayer.stop()
    }
}
