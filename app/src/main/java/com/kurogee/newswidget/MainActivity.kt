package com.kurogee.newswidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kurogee.newswidget.databinding.ActivityMainBinding
import com.kurogee.newswidget.widget.NewsWidgetProvider

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        setupRecyclerView()
        observeNews()
        
        viewModel.fetchNews()
    }

    private fun setupUI() {
        supportActionBar?.title = "NHKニュースウィジェット"
        
        // Add refresh button action
        binding.refreshButton?.setOnClickListener {
            refreshWidgets()
            viewModel.fetchNews()
            Toast.makeText(this, "NHKニュースを更新しています", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        val repository = NewsRepository()
        val factory = NewsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter { newsItem ->
            // Handle news item click - open URL
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(newsItem.url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeNews() {
        viewModel.news.observe(this) { newsList ->
            adapter.submitList(newsList)
        }
        
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar?.visibility = if (isLoading) 
                android.view.View.VISIBLE else android.view.View.GONE
        }
    }
    
    private fun refreshWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgetComponent = ComponentName(this, NewsWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
        
        for (widgetId in widgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list_view)
        }
    }
}