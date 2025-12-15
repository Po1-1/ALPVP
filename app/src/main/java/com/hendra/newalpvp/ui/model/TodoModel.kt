package com.hendra.newalpvp.ui.model

data class TodoRequest(val title: String, val time: String, val isReminder: Boolean)

data class TodoResponse(
    val id: String,
    val title: String,
    val time: String,
    val isReminder: Boolean,
    val isCompleted: Boolean
)