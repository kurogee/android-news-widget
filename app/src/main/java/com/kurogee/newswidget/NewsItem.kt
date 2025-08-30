package com.kurogee.newswidget

data class NewsItem(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val url: String,
    val publishedAt: String
)