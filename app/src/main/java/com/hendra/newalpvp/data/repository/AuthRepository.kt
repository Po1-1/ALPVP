package com.hendra.newalpvp.data.repository

import com.hendra.newalpvp.data.service.ApiService
import com.hendra.newalpvp.data.util.safeApiCall
import com.hendra.newalpvp.ui.model.*

class AuthRepository(private val api: ApiService) {
    suspend fun register(req: RegisterRequest) = safeApiCall { api.register(req) }
    suspend fun login(req: LoginRequest) = safeApiCall { api.login(req) }
}