package com.kurogee.newswidget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// ニュースAPIのレスポンスを表すデータクラス
data class NewsResponse(
    val articles: List<Article>
)

data class Article(
    val title: String,
    val url: String
)

// Retrofitインターフェース
interface NewsApi {
    @GET("top-headlines?country=jp&apiKey=YOUR_API_KEY")
    suspend fun getTopHeadlines(): NewsResponse
}

// ニュースを取得するサービスクラス
class NewsService {
    private val api: NewsApi
    
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        api = retrofit.create(NewsApi::class.java)
    }
    
    suspend fun getLatestNews(): List<Article> = withContext(Dispatchers.IO) {
        try {
            // 実際のAPIからニュースを取得
            // api.getTopHeadlines().articles
            
            // APIキーが必要なので、デモのためのダミーデータを返す
            listOf(
                Article("速報: 新型コロナワクチン接種率が80%に到達", "https://example.com/news1"),
                Article("東京都で新たなスマートシティプロジェクトが開始", "https://example.com/news2"),
                Article("大手テック企業が新型AIアシスタントを発表", "https://example.com/news3"),
                Article("今年の夏は記録的な暑さになる見込み、専門家が警告", "https://example.com/news4"),
                Article("新しい教育方針が全国の学校で導入へ", "https://example.com/news5")
            )
        } catch (e: Exception) {
            // エラー時は空のリストを返す
            emptyList()
        }
    }
}