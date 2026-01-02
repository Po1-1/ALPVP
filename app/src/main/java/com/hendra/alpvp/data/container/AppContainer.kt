package com.hendra.alpvp.data.container

import android.content.Context
import com.hendra.alpvp.data.repository.*
import com.hendra.alpvp.data.service.ApiService
import com.hendra.alpvp.data.container.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {

    // --- PILIH SALAH SATU BASE URL DI BAWAH ---

    // 1. GANTI INI jika pakai EMULATOR Android Studio
    private val BASE_URL = "http://10.0.2.2:3000/"

    // 2. GANTI INI jika pakai HP FISIK (Ganti angkanya dengan IP Laptop kamu)
    // private val BASE_URL = "http://192.168.1.8:3000/"

    // Konfigurasi Client agar log request terlihat di Logcat
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Menampilkan Body JSON di Logcat
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
            // Ambil token secara dinamis
            TokenManager.getToken(context)?.let { token ->
                request.addHeader("Authorization", token)
            }
            chain.proceed(request.build())
        }
        // Tambahkan timeout jika koneksi lambat
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    // Inisialisasi Repositories
    val authRepository by lazy { AuthRepository(apiService) }
    val financeRepository by lazy { FinanceRepository(apiService) }
    val eventRepository by lazy { EventRepository(apiService) }
    val todoRepository by lazy { TodoRepository(apiService) }
    val sleepRepository by lazy { SleepRepository(apiService) }
    val weatherRepository by lazy { WeatherRepository(apiService) }
}
