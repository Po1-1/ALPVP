package com.hendra.newalpvp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.hendra.newalpvp.data.container.AppContainer

class MomentumApplication : Application() {

    // Instance Container Global
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Container
        container = AppContainer(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val todoChannel = NotificationChannel(
                "TODO_CHANNEL", "To-Do Reminder", NotificationManager.IMPORTANCE_HIGH
            )
            val alarmChannel = NotificationChannel(
                "ALARM_CHANNEL", "Alarm Suara", NotificationManager.IMPORTANCE_HIGH
            )
            val urgentChannel = NotificationChannel(
                "ALARM_URGENT", "Alarm Bangun", NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(todoChannel)
            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(urgentChannel)
        }
    }
}