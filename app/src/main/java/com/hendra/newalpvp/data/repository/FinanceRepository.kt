package com.hendra.newalpvp.data.repository

import com.hendra.newalpvp.data.service.ApiService
import com.hendra.newalpvp.data.util.safeApiCall
import com.hendra.newalpvp.ui.model.TransactionRequest

class FinanceRepository(private val api: ApiService) {
    suspend fun createTransaction(req: TransactionRequest) = safeApiCall { api.createTransaction(req) }
    suspend fun getTransactions() = safeApiCall { api.getTransactions() }
}