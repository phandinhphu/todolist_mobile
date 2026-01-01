package com.example.todolist.ui.screen.alarm

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.todolist.data.broadcast.ReminderReceiver
import com.example.todolist.ui.theme.ToDoListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private val viewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        showOnLockScreen()
        super.onCreate(savedInstanceState)

        val taskTitle = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_TITLE) ?: "Task Reminder"
        val taskDesc = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_DESC)
        val category = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_CATEGORY)
        val priority = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_PRIORITY)

        // Start playing alarm immediately
        viewModel.startAlarm()

        setContent {
            ToDoListTheme {
                AlarmScreen(
                        title = taskTitle,
                        description = taskDesc,
                        category = category,
                        priority = priority,
                        onDismiss = {
                            viewModel.stopAlarm()
                            finishAndRemoveTask() // Completely close
                        }
                )
            }
        }
    }

    private fun showOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAlarm()
    }
}
