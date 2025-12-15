package com.hendra.alpvp.data.service

import com.hendra.alpvp.ui.model.WebResponse
import com.hendra.alpvp.ui.model.*
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

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

    //EVENT

    @GET("api/event")
    suspend fun getAllEvents(): Response<WebResponse<List<EventResponse>>>

    @GET("api/event/{id}")
    suspend fun getEvent(
        @Path("id") id: Int
    ): Response<WebResponse<EventResponse>>

    @POST("api/event")
    suspend fun createEvent(
        @Body request: EventRequest
    ): Response<WebResponse<EventResponse>>

    @PUT("api/event/{event_id}")
    suspend fun updateEvent(
        @Path("event_id") eventId: Int,
        @Body request: EventRequest
    ): Response<WebResponse<EventResponse>>

    @DELETE("api/event/{event_id}")
    suspend fun deleteEvent(
        @Path("event_id") eventId: Int
    ): Response<WebResponse<String>>



}