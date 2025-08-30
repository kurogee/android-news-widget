package com.kurogee.newswidget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    
    private val _news = MutableLiveData<List<NewsItem>>()
    val news: LiveData<List<NewsItem>> = _news
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    fun fetchNews() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val newsList = repository.getLatestNews()
                _news.value = newsList
            } catch (e: Exception) {
                // Handle error
                _news.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}