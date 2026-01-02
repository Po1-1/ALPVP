package com.hendra.alpvp.ui.model

data class AlarmRequest(
    val time: String,
    val label: String,
    val days: List<Boolean>,
    val isActive: Boolean = true
)

data class ToggleAlarmRequest(val isActive: Boolean)

data class AlarmResponse(
    val id: String,
    val time: String,
    val label: String,
    val days: List<Boolean>,
    val isActive: Boolean,
    val userId: String
) {
    fun getTimeString(): String = time
    fun getDaysString(): String {
        val daysName = listOf("M", "S", "S", "R", "K", "J", "S")
        val activeDays = days.mapIndexed { i, active -> if (active) daysName[i] else null }.filterNotNull()
        return if (activeDays.isEmpty()) "Sekali" else if (activeDays.size == 7) "Setiap Hari" else activeDays.joinToString(" ")
    }
}