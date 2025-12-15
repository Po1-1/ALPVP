package com.hendra.newalpvp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hendra.newalpvp.R
import kotlinx.coroutines.*

class TodoReminderService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val todoTitle = intent?.getStringExtra("TODO_TITLE") ?: "Waktunya mengerjakan tugas!"

        // 1. Tampilkan Notifikasi agar service berjalan (Foreground Service)
        showNotification(todoTitle)

        // 2. Jalankan logika bunyi 3 kali di background
        serviceScope.launch {
            repeat(3) { // Bunyi 3 kali
                playSound()
                delay(3000) // Jeda 3 detik
            }
            stopSelf() // Matikan service otomatis setelah selesai
        }

        return START_NOT_STICKY
    }

    private fun showNotification(title: String) {
        val channelId = "TODO_CHANNEL"
        val manager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "To-Do Reminder", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("PENGINGAT TUGAS")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(200, notification)
    }

    private fun playSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, uri).apply {
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}