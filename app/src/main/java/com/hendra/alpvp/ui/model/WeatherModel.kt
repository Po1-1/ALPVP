package com.hendra.alpvp.ui.model

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherDescription>,
    val name: String
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)