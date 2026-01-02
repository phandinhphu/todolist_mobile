package com.example.todolist.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.todolist.R
import com.example.todolist.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TodayTaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )

        val getTodayTasksUseCase = entryPoint.getTodayTasksUseCase()
        val getWidgetEnabledUseCase = entryPoint.getWidgetEnabledUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            if (getWidgetEnabledUseCase().firstOrNull() != true) {
                return@launch
            }

            val tasks = getTodayTasksUseCase()

            val views = RemoteViews(
                context.packageName,
                R.layout.widget_today_task
            )

            // Cập nhật số lượng task
            views.setTextViewText(
                R.id.tvTaskCount,
                "Hôm nay: ${tasks.size} task chưa hoàn thành"
            )

            // Ẩn tất cả task layouts ban đầu
            views.setViewVisibility(R.id.layoutTask1, View.GONE)
            views.setViewVisibility(R.id.layoutTask2, View.GONE)
            views.setViewVisibility(R.id.layoutTask3, View.GONE)
            views.setViewVisibility(R.id.tvEmptyState, View.GONE)

            if (tasks.isEmpty()) {
                // Hiển thị empty state
                views.setViewVisibility(R.id.tvEmptyState, View.VISIBLE)
            } else {
                // Hiển thị task 1
                views.setViewVisibility(R.id.layoutTask1, View.VISIBLE)
                views.setTextViewText(R.id.tvTask1Title, tasks[0].title)
                views.setTextViewText(
                    R.id.tvTask1Deadline,
                    "📅 ${formatDate(tasks[0].dueDate)}"
                )

                // Hiển thị task 2
                if (tasks.size >= 2) {
                    views.setViewVisibility(R.id.layoutTask2, View.VISIBLE)
                    views.setTextViewText(R.id.tvTask2Title, tasks[1].title)
                    views.setTextViewText(
                        R.id.tvTask2Deadline,
                        "📅 ${formatDate(tasks[1].dueDate)}"
                    )
                }

                // Hiển thị task 3
                if (tasks.size >= 3) {
                    views.setViewVisibility(R.id.layoutTask3, View.VISIBLE)
                    views.setTextViewText(R.id.tvTask3Title, tasks[2].title)
                    views.setTextViewText(
                        R.id.tvTask3Deadline,
                        "📅 ${formatDate(tasks[2].dueDate)}"
                    )
                }
            }

            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }

    private fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "Không có deadline"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

}