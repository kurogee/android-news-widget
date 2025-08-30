package com.kurogee.newswidget

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewsRepository {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // NHKニュースのRSSフィード
    private val nhkRssUrl = "https://www.nhk.or.jp/rss/news/cat0.xml"
    
    suspend fun getLatestNews(): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            Log.d("NewsRepository", "Fetching NHK RSS feed")
            
            val request = Request.Builder()
                .url(nhkRssUrl)
                .addHeader("User-Agent", "NewsWidget/1.0")
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                Log.d("NewsRepository", "Successfully fetched RSS")
                parseRssResponse(responseBody)
            } else {
                Log.e("NewsRepository", "Failed to fetch RSS: ${response.code}")
                getFallbackNews()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching NHK RSS", e)
            getFallbackNews()
        }
    }
    
    private fun parseRssResponse(rssContent: String): List<NewsItem> {
        return try {
            val doc: Document = Jsoup.parse(rssContent, "", org.jsoup.parser.Parser.xmlParser())
            val items = doc.select("item")
            
            val newsList = mutableListOf<NewsItem>()
            
            for (item in items.take(10)) { // 最大10件
                val title = item.select("title").text()
                val description = item.select("description").text()
                val link = item.select("link").text()
                val pubDate = item.select("pubDate").text()
                
                if (title.isNotEmpty()) {
                    newsList.add(
                        NewsItem(
                            title = title,
                            description = description.ifEmpty { "詳細は記事をご覧ください" },
                            imageUrl = null,
                            url = link.ifEmpty { "https://www.nhk.or.jp" },
                            publishedAt = formatRssDate(pubDate)
                        )
                    )
                }
            }
            
            Log.d("NewsRepository", "Parsed ${newsList.size} news items from NHK RSS")
            newsList
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error parsing RSS", e)
            getFallbackNews()
        }
    }
    
    private fun formatRssDate(rssDate: String): String {
        return try {
            if (rssDate.isEmpty()) return getCurrentTimeString()
            
            // RSSの日付フォーマット例: "Mon, 30 Aug 2025 10:00:00 +0900"
            val rssFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            val date = rssFormat.parse(rssDate)
            
            if (date != null) {
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                outputFormat.format(date)
            } else {
                getCurrentTimeString()
            }
        } catch (e: Exception) {
            Log.w("NewsRepository", "Failed to parse date: $rssDate", e)
            getCurrentTimeString()
        }
    }
    
    private fun getFallbackNews(): List<NewsItem> {
        Log.d("NewsRepository", "Using fallback news data")
        return listOf(
            NewsItem(
                title = "NHKニュースを取得中...",
                description = "インターネット接続を確認して、しばらくお待ちください。",
                imageUrl = null,
                url = "https://www.nhk.or.jp",
                publishedAt = getCurrentTimeString()
            ),
            NewsItem(
                title = "ニュースウィジェット",
                description = "最新のNHKニュースをお届けします。ウィジェットを更新してください。",
                imageUrl = null,
                url = "https://www.nhk.or.jp",
                publishedAt = getTimeString(-1)
            )
        )
    }
    
    private fun getCurrentTimeString(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return format.format(Date())
    }
    
    private fun getTimeString(hoursAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, hoursAgo)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return format.format(calendar.time)
    }
}