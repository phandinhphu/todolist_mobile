package com.example.todolist.util

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.todolist.ui.widget.TodayTaskWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun updateWidget() {
        val intent = Intent(context, TodayTaskWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, TodayTaskWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
        
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context.sendBroadcast(intent)
    }
}
