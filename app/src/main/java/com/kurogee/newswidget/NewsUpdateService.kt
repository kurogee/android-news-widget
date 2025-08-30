package com.kurogee.newswidget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NewsUpdateService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val newsService = NewsService()
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNews()
        return START_STICKY
    }
    
    private fun updateNews() {
        serviceScope.launch {
            val news = newsService.getLatestNews()
            
            if (news.isNotEmpty()) {
                // ウィジェットを更新するインテントを作成
                val intent = Intent(this@NewsUpdateService, NewsWidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                
                val ids = AppWidgetManager.getInstance(application)
                    .getAppWidgetIds(ComponentName(application, NewsWidgetProvider::class.java))
                
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                intent.putParcelableArrayListExtra("news_list", ArrayList(news))
                
                sendBroadcast(intent)
            }
            
            // サービスを停止
            stopSelf()
        }
    }
    
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}