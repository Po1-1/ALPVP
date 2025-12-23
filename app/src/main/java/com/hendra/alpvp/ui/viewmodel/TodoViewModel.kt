package com.hendra.alpvp.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.TodoRepository
import com.hendra.alpvp.data.service.TodoAlarmReceiver
import com.hendra.alpvp.ui.model.TodoRequest
import com.hendra.alpvp.ui.model.TodoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<TodoResponse>>(emptyList())
    val todos = _todos.asStateFlow()

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            repository.getTodos().onSuccess { response ->
                _todos.value = response.data
            }
        }
    }

    fun addTodo(context: Context, title: String, dateTimeString: String) {
        viewModelScope.launch {
            // dateTimeString format: "yyyy-MM-dd HH:mm"
            val req = TodoRequest(title, dateTimeString, isReminder = true)
            repository.createTodo(req).onSuccess {
                loadTodos() // Reload data dari server
                scheduleTodoAlarm(context, title, dateTimeString) // Set Alarm
                Toast.makeText(context, "Pengingat diset!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toggleTodo(id: String) {
        viewModelScope.launch {
            repository.toggleTodo(id).onSuccess { loadTodos() }
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            repository.deleteTodo(id).onSuccess { loadTodos() }
        }
    }

    // --- LOGIC ALARM MANAGER ---
    private fun scheduleTodoAlarm(context: Context, title: String, dateTimeString: String) {
        try {
            // Parsing String Tanggal ke Miliseconds
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(dateTimeString) ?: return
            val timeInMillis = date.time

            if (timeInMillis <= System.currentTimeMillis()) return // Jangan set jika waktu lampau

            val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
                putExtra("TODO_TITLE", title)
            }

            // ID Request Code unik berdasarkan hashcode judul agar alarm tidak saling menimpa
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                title.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Izin schedule exact alarm bisa ditambahkan di sini jika perlu
                return
            }

            // Set Alarm Tepat Waktu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                TodoViewModel(app.container.todoRepository)
            }
        }
    }
}
