package com.kurogee.newswidget.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.kurogee.newswidget.NewsItem
import com.kurogee.newswidget.NewsRepository
import com.kurogee.newswidget.R
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class NewsWidgetViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private val repository = NewsRepository()
    private var newsList: List<NewsItem> = emptyList()

    override fun onCreate() {
        Log.d("NewsWidgetViewsFactory", "onCreate called")
    }

    override fun onDataSetChanged() {
        Log.d("NewsWidgetViewsFactory", "onDataSetChanged called")
        // Fetch latest news
        newsList = runBlocking {
            try {
                repository.getLatestNews()
            } catch (e: Exception) {
                Log.e("NewsWidgetViewsFactory", "Error fetching news", e)
                emptyList()
            }
        }
        Log.d("NewsWidgetViewsFactory", "Loaded ${newsList.size} news items")
    }

    override fun onDestroy() {
        Log.d("NewsWidgetViewsFactory", "onDestroy called")
    }

    override fun getCount(): Int = newsList.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_news_item)
        
        if (position < newsList.size) {
            val newsItem = newsList[position]
            
            // Set title
            views.setTextViewText(R.id.widget_title, newsItem.title)
            
            // Set description
            views.setTextViewText(R.id.widget_description, newsItem.description)
            
            // Set formatted time
            val timeAgo = getTimeAgo(newsItem.publishedAt)
            views.setTextViewText(R.id.widget_time, timeAgo)
            
            // Set click intent to open news URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
            views.setOnClickFillInIntent(R.id.widget_title, intent)
        }
        
        return views
    }

    override fun getLoadingView(): RemoteViews? {
        val views = RemoteViews(context.packageName, R.layout.widget_news_item)
        views.setTextViewText(R.id.widget_title, "読み込み中...")
        views.setTextViewText(R.id.widget_description, "最新ニュースを取得しています")
        views.setTextViewText(R.id.widget_time, "")
        return views
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
    
    private fun getTimeAgo(publishedAt: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date = format.parse(publishedAt)
            
            if (date != null) {
                val now = System.currentTimeMillis()
                val published = date.time
                val diff = now - published
                
                val minutes = diff / (1000 * 60)
                val hours = diff / (1000 * 60 * 60)
                val days = diff / (1000 * 60 * 60 * 24)
                
                when {
                    minutes < 60 -> "${minutes}分前"
                    hours < 24 -> "${hours}時間前"
                    days < 7 -> "${days}日前"
                    else -> {
                        val displayFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                        displayFormat.format(date)
                    }
                }
            } else {
                "先ほど"
            }
        } catch (e: Exception) {
            Log.e("NewsWidgetViewsFactory", "Error parsing date: $publishedAt", e)
            "先ほど"
        }
    }
}