package com.hendra.newalpvp.data.repository

import com.hendra.newalpvp.data.service.ApiService
import com.hendra.newalpvp.data.util.safeApiCall
import com.hendra.newalpvp.ui.model.AlarmRequest
import com.hendra.newalpvp.ui.model.ToggleAlarmRequest

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