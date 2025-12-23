package com.hendra.alpvp.data.repository

import com.hendra.alpvp.data.service.ApiService
import com.hendra.alpvp.data.util.safeApiCall
import com.hendra.alpvp.ui.model.AlarmRequest
import com.hendra.alpvp.ui.model.ToggleAlarmRequest

class SleepRepository(private val api: ApiService) {
    suspend fun getAlarms() = safeApiCall { api.getAlarms() }
    suspend fun createAlarm(time: String, label: String, days: List<Boolean>) = safeApiCall {
        api.createAlarm(AlarmRequest(time, label, days))
    }
    suspend fun toggleAlarm(id: String, isActive: Boolean) = safeApiCall {
        api.toggleAlarm(id, ToggleAlarmRequest(isActive))
    }
    suspend fun deleteAlarm(id: String) = safeApiCall { api.deleteAlarm(id) }
}