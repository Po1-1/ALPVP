package com.hendra.alpvp.data.util

import org.json.JSONObject
import retrofit2.Response

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return try{
        val response = call()
        if (response.isSuccessful && response.body() != null){
            Result.success(response.body()!!) // Untuk mengembalikan data jika berhasil
        }else{
            val errorBody = response.errorBody()?.string() // Jika gagal, ambil pesan kesalahan dari body
            val errorMessage = try{ // Asumsi format error server: { "message": "Password salah", ... }
                val jsonObject = JSONObject(errorBody ?: "") // Parsing JSON untuk pesan kesalahan
                jsonObject.getString("message") // Mengambil pesan kesalahan dari JSON
            } catch (e: Exception) { // Jika gagal, gunakan pesan kesalahan dari body
                "Terjadi Kesalahan: ${response.code()} ${response.message()}" // Jika gagal, gunakan pesan kesalahan dari body
            }
            Result.failure(Exception(errorMessage))
        }
    } catch (e:Exception){ // Jika terjadi error koneksi/jaringan (misal: tidak ada internet)
        Result.failure(Exception("Gagal Terhubung ke server. Cek Koneksi "))
    }
}