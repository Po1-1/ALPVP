package com.hendra.alpvp.ui.model

data class AlarmRequest(
    val time: String,
    val label: String,
    val days: List<Boolean>,
    val isActive: Boolean = true
)

data class ToggleAlarmRequest(val isActive: Boolean)

data class AlarmResposne(
    val id: String,
    val time: String,
    val label: String,
    val days: List<Boolean>,
    val isActive: Boolean,
    val userId: String
)


