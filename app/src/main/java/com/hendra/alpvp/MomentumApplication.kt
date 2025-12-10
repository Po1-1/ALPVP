package com.hendra.alpvp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MomentumApplication : Application() {

    // Instance Container Global
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Container
        container = AppContainer(this)
    }

}