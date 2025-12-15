package com.hendra.alpvp.ui.model

data class LoginRequest(
    val email: String,
    val pass: String
)

data class RegisterRequest(
    val email: String,
    val pass: String,
    val username: String
)

data class UserResponse(
    val token: String,
    val email: String,
    val username: String
)
