package com.hendra.alpvp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hendra.alpvp.R
import kotlin.jvm.java

class AlarmSoundService : Service() {
    private var ringtone: Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1. Buat Notifikasi Foreground agar Service tidak dimatikan sistem
        val channelId = "ALARM_CHANNEL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Suara", NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("BANGUN!")
            .setContentText("Selesaikan soal matematika untuk mematikan alarm.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Pastikan icon ini ada
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()

        // ID 101 bebas, asal unik
        startForeground(101, notification)

        // 2. Putar Suara Alarm
        playAlarmSound()

        return START_STICKY
    }

    private fun playAlarmSound() {
        try {
            // Mengambil uri suara alarm default HP
            var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                // Fallback ke suara notifikasi jika tidak ada suara alarm
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)

            // Konfigurasi agar suara keras (Alarm Usage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ringtone?.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true // Biar bunyi terus menerus
            }

            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Matikan suara saat service dihentikan (saat soal matematika selesai)
        ringtone?.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}