package com.hendra.alpvp.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.ui.model.AlarmResponse
import com.hendra.alpvp.data.repository.SleepRepository
import com.hendra.alpvp.data.service.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class SleepViewModel(private val repository: SleepRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmResponse>>(emptyList())
    val alarms = _alarms.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.getAlarms().onSuccess { response ->
                _alarms.value = response.data
            }.onFailure { error ->
                Log.e("SleepViewModel", "GAGAL Load Data: ${error.message}")
            }
        }
    }

    fun addAlarm(context: Context, hour: Int, minute: Int, label: String, days: List<Boolean>) {
        val timeString = String.format("%02d:%02d", hour, minute)
        viewModelScope.launch {
            repository.createAlarm(timeString, label, days).onSuccess { response ->
                val newAlarm = response.data
                loadData()
                scheduleSystemAlarm(context, hour, minute, days, newAlarm.id.hashCode())
                Toast.makeText(context, "Alarm tersimpan!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toggleAlarm(context: Context, id: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(id, isActive).onSuccess { response ->
                val updatedAlarm = response.data
                loadData()

                val parts = updatedAlarm.time.split(":")
                val h = parts[0].toInt()
                val m = parts[1].toInt()

                if (isActive) {
                    scheduleSystemAlarm(context, h, m, updatedAlarm.days, updatedAlarm.id.hashCode())
                    Toast.makeText(context, "Alarm Nyala", Toast.LENGTH_SHORT).show()
                } else {
                    cancelSystemAlarm(context, updatedAlarm.id.hashCode())
                    Toast.makeText(context, "Alarm Mati", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteAlarm(context: Context, id: String) {
        viewModelScope.launch {
            repository.deleteAlarm(id).onSuccess {
                cancelSystemAlarm(context, id.hashCode())
                loadData()
            }
        }
    }

    private fun scheduleSystemAlarm(context: Context, hour: Int, minute: Int, days: List<Boolean>, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                return
            }
        }

        val triggerTime = calculateNextAlarmTime(hour, minute, days) // Hitung kapan alarm bunyi

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Log.d("SleepViewModel", "Alarm dijadwalkan: ${java.util.Date(triggerTime)}")
        } catch (e: Exception) {
            Log.e("SleepViewModel", "Gagal Jadwal: ${e.message}")
        }
    }

    private fun cancelSystemAlarm(context: Context, requestCode: Int) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(pendingIntent)
        } catch (e: Exception) {
            Log.e("SleepViewModel", "Gagal Batal: ${e.message}")
        }
    }

    private fun calculateNextAlarmTime(hour: Int, minute: Int, days: List<Boolean>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val isRepeat = days.contains(true)

        if (!isRepeat) {
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        } else {
            val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)
            for (i in 0..7) {
                val checkIndex = (currentDayOfWeek + i - 1) % 7
                // Pastikan akses array aman
                if (checkIndex < days.size && days[checkIndex]) {
                    if (i == 0 && target.before(now)) {
                        continue
                    }
                    target.add(Calendar.DAY_OF_YEAR, i)
                    return target.timeInMillis
                }
            }
            return target.timeInMillis
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                SleepViewModel(app.container.sleepRepository)
            }
        }
    }
}