package com.hendra.newalpvp.data.repository

import com.hendra.newalpvp.data.service.ApiService
import com.hendra.newalpvp.ui.model.WeatherResponse

class WeatherRepository(private val api: ApiService) {


    private val API_KEY = "f2f8807fdad87323534d6b7cfe3cc696"

    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"
            val response = api.getCurrentWeather(url)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal load cuaca: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}