package com.hendra.alpvp.data.service

import com.hendra.alpvp.ui.model.WebResponse
import com.hendra.alpvp.ui.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- AUTH ---
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<WebResponse<UserResponse>>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<WebResponse<UserResponse>>

    // FINANCE
    @POST("api/finance")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<WebResponse<TransactionResponse>>

    @GET("api/finance")
    suspend fun getTransactions(): Response<WebResponse<List<TransactionResponse>>>
}