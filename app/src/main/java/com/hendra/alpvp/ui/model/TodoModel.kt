package com.hendra.alpvp.ui.model

data class TodoRequest(
    val title: String,
    val description: String,
    val categoryId: String? = null
)

data class TodoResponse(
    val id: String,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val userId: String,
    val categoryId: String? = null,
    val createdAt: String,
    val updatedAt: String
)
