package com.hendra.alpvp.ui.model

data class AlarmRequest(
    val time: String,
    val label: String,
    val isActive: Boolean
)

data class AlarmResponse(
    val id: String,
    val time: String,
    val label: String,
    val isActive: Boolean,
    val userId: String
)

data class ToggleAlarmRequest(
    val isActive: Boolean
)
