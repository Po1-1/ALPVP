package com.hendra.alpvp.ui.model

data class EventRequest(
    val date: String,
    val eventName: String,
    val startTime: String,
    val endTime: String
)

data class EventResponse(
    val id: String,
    val date: String,
    val eventName: String,
    val startTime: String,
    val endTime: String
)
