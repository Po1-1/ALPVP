package com.hendra.alpvp.data.util

import org.json.JSONObject
import retrofit2.Response

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return try {
        val response = call()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                val jsonObject = JSONObject(errorBody ?: "")
                jsonObject.getString("message")
            } catch (e: Exception) {
                "Terjadi kesalahan: ${response.code()} ${response.message()}"
            }
            Result.failure(Exception(errorMessage))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Gagal terhubung ke server. Cek koneksi internet."))
    }
}