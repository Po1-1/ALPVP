package com.hendra.alpvp.data.repository

import com.hendra.alpvp.data.service.ApiService
import com.hendra.alpvp.data.util.safeApiCall
import com.hendra.alpvp.ui.model.TransactionRequest

class FinanceRepository(private val api: ApiService) {
    suspend fun createTransaction(req: TransactionRequest) = safeApiCall { api.createTransaction(req) }
    suspend fun getTransactions() = safeApiCall { api.getTransactions() }
}