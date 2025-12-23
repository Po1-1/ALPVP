package com.hendra.alpvp.ui.model

data class RegisterRequest(val username: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val token: String? = null
)