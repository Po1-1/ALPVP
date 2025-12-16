package com.hendra.alpvp.ui.model
data class TransactionRequest(
    val type: String, // "INCOME" atau "EXPENSE"
    val amount: Double,
    val category: String,
    val date: String
)

data class TransactionResponse(
    val id: String,
    val type: String,
    val amount: Double,
    val category: String,
    val date: String,
    val userId: String
)

data class FinanceUiState(
    val transactions: List<TransactionResponse> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = false
)