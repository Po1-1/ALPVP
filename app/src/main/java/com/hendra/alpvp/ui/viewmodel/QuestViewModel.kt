package com.hendra.alpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

// State untuk UI
data class QuestState(
    val time: String = "00:00",
    val amPm: String = "AM",
    val greeting: String = "GOOD MORNING",
    val temp: String = "--°C",
    val weatherIconUrl: String? = null,
    val num1: Int = 0,
    val num2: Int = 0,
    val userAnswer: String = "",
    val isError: Boolean = false
)

class QuestViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestState())
    val uiState = _uiState.asStateFlow()

    init {
        generateMathProblem()
        startClock()
        fetchWeather()
    }

    private fun generateMathProblem() {
        val n1 = Random.nextInt(10, 50)
        val n2 = Random.nextInt(5, 20)
        _uiState.value = _uiState.value.copy(num1 = n1, num2 = n2)
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val timeStr = SimpleDateFormat("h:mm", Locale.getDefault()).format(calendar.time)
                val amPmStr = SimpleDateFormat("a", Locale.getDefault()).format(calendar.time)

                val greetingText = when (hour) {
                    in 5..11 -> "GOOD MORNING"
                    in 12..17 -> "GOOD AFTERNOON"
                    else -> "GOOD NIGHT"
                }

                _uiState.value = _uiState.value.copy(
                    time = timeStr,
                    amPm = amPmStr,
                    greeting = greetingText
                )
                delay(1000) // Update setiap detik
            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            // Hardcode Lat/Lon Surabaya (atau ganti lokasi user jika ada izin lokasi)
            // Lat: -7.2575, Lon: 112.7521 (Surabaya)
            repository.getWeather(-7.2575, 112.7521).onSuccess { resp ->
                val iconCode = resp.weather.firstOrNull()?.icon ?: "01d"
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"
                val tempStr = "${resp.main.temp.toInt()}°C"

                _uiState.value = _uiState.value.copy(
                    temp = tempStr,
                    weatherIconUrl = iconUrl
                )
            }
        }
    }

    fun onAnswerChange(newAnswer: String) {
        _uiState.value = _uiState.value.copy(userAnswer = newAnswer, isError = false)
    }

    fun submitAnswer(onSuccess: () -> Unit) {
        val state = _uiState.value
        val correctAnswer = state.num1 + state.num2
        if (state.userAnswer == correctAnswer.toString()) {
            onSuccess()
        } else {
            _uiState.value = _uiState.value.copy(isError = true, userAnswer = "")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                QuestViewModel(app.container.weatherRepository)
            }
        }
    }
}