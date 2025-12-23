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

    // --- FINANCE ---
    @POST("api/finance")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<WebResponse<TransactionResponse>>

    @GET("api/finance")
    suspend fun getTransactions(): Response<WebResponse<List<TransactionResponse>>>

    // --- TODO ---
    @GET("api/todos")
    suspend fun getTodos(): Response<WebResponse<List<TodoResponse>>>

    @POST("api/todos")
    suspend fun createTodo(@Body request: TodoRequest): Response<WebResponse<TodoResponse>>

    @PATCH("api/todos/{id}/toggle")
    suspend fun toggleTodo(@Path("id") id: String): Response<WebResponse<TodoResponse>>

    @DELETE("api/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String): Response<WebResponse<Any>>

    // --- ALARM (SLEEP) ---
    @GET("api/alarms")
    suspend fun getAlarms(): Response<WebResponse<List<AlarmResponse>>>

    @POST("api/alarms")
    suspend fun createAlarm(@Body request: AlarmRequest): Response<WebResponse<AlarmResponse>>

    @PATCH("api/alarms/{id}/toggle")
    suspend fun toggleAlarm(@Path("id") id: String, @Body request: ToggleAlarmRequest): Response<WebResponse<AlarmResponse>>

    @DELETE("api/alarms/{id}")
    suspend fun deleteAlarm(@Path("id") id: String): Response<WebResponse<Any>>

    @GET
    suspend fun getCurrentWeather(
        @Url url: String
    ): Response<WeatherResponse>
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