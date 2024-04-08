package com.nelu.scrapperapp

import android.app.Application
import com.nelu.scrapper.Scrapper

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Scrapper.init(this)
    }
}