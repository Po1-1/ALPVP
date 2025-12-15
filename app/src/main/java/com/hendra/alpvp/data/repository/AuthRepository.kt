package com.hendra.alpvp.data.repository

import com.hendra.alpvp.data.service.ApiService
import com.hendra.alpvp.data.util.safeApiCall
import com.hendra.alpvp.ui.model.LoginRequest
import com.hendra.alpvp.ui.model.RegisterRequest

class AuthRepository(private val api: ApiService) {
    suspend fun register(req: RegisterRequest) = safeApiCall { api.register(req) }
    suspend fun login(req: LoginRequest) = safeApiCall { api.login(req) }
}
