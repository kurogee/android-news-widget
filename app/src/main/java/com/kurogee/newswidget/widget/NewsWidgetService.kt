package com.kurogee.newswidget.widget

import android.content.Intent
import android.widget.RemoteViewsService

class NewsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NewsWidgetViewsFactory(this.applicationContext)
    }
}