package com.hendra.alpvp.data.repository;

import com.hendra.alpvp.data.service.ApiService;
import com.hendra.alpvp.data.util.safeApiCall
import com.hendra.alpvp.ui.model.EventRequest

class EventRepository (private val api: ApiService) {
    suspend fun getAllEvents() = safeApiCall { api.getAllEvents() }
    suspend fun getEvent(id: Int) = safeApiCall { api.getEvent(id) }
    suspend fun createEvent(request: EventRequest) = safeApiCall { api.createEvent(request) }
    suspend fun updateEvent(eventId: Int, request: EventRequest) = safeApiCall { api.updateEvent(eventId, request) }
    suspend fun deleteEvent(eventId: Int) = safeApiCall { api.deleteEvent(eventId) }
}
