package com.kurogee.newswidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.kurogee.newswidget.MainActivity
import com.kurogee.newswidget.R

class NewsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("NewsWidgetProvider", "onUpdate called for ${appWidgetIds.size} widgets")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.d("NewsWidgetProvider", "onEnabled called")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.d("NewsWidgetProvider", "onDisabled called")
        super.onDisabled(context)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d("NewsWidgetProvider", "Updating widget $appWidgetId")
        
        val views = RemoteViews(context.packageName, R.layout.news_widget)
        
        // Set up the intent that starts the NewsWidgetService
        val intent = Intent(context, NewsWidgetService::class.java)
        views.setRemoteAdapter(R.id.widget_list_view, intent)
        
        // Set up the intent template for list items
        val clickIntent = Intent(Intent.ACTION_VIEW)
        val clickPendingIntent = PendingIntent.getActivity(
            context, 0, clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntent)
        
        // Set up click handler for the header to open main app
        val headerIntent = Intent(context, MainActivity::class.java)
        val headerPendingIntent = PendingIntent.getActivity(
            context, 0, headerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_list_view, headerPendingIntent)
        
        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
        
        // Notify widget that data has changed
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view)
    }
}