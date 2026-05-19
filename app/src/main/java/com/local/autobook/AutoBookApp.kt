package com.local.autobook

import android.app.Application
import com.local.autobook.data.AppContainer

class AutoBookApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
