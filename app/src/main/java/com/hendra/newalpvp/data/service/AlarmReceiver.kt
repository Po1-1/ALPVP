package com.hendra.newalpvp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hendra.newalpvp.R
import com.hendra.newalpvp.ui.view.QuestActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // 1. Nyalakan Service Suara (Biar Berisik terus menerus sampai dimatikan)
        val serviceIntent = Intent(context, AlarmSoundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // 2. Tampilkan Notifikasi Full Screen (QuestActivity)
        showAlarmNotification(context)
    }

    private fun showAlarmNotification(context: Context) {
        val channelId = "ALARM_URGENT"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Bangun", NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null, null) // Suara dihandle oleh service, jadi di sini silent
            manager.createNotificationChannel(channel)
        }

        // Intent untuk membuka QuestActivity (Layar Soal Matematika)
        val fullScreenIntent = Intent(context, QuestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Pastikan resource icon ada
            .setContentTitle("WAKTUNYA BANGUN!")
            .setContentText("Selesaikan soal matematika untuk mematikan alarm.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // KUNCI UTAMA: Agar muncul di Lock Screen
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        manager.notify(999, notification)
    }
}