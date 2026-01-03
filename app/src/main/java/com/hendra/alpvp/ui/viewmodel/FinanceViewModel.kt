package com.hendra.alpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.FinanceRepository
import com.hendra.alpvp.ui.model.FinanceUiState
import com.hendra.alpvp.ui.model.TransactionRequest
import com.hendra.alpvp.ui.model.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState = _uiState.asStateFlow()
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getTransactions().onSuccess { response ->
                val transactions = response.data

                // 3. Logika Bisnis (Perhitungan) dipindah ke sini
                val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
                val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                val balance = totalIncome - totalExpense

                _uiState.update {
                    it.copy(
                        transactions = transactions,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        balance = balance,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createTransaction(type: String, amount: Double, category: String) {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date())
            val req = TransactionRequest(type, amount, category, currentDate)

            repository.createTransaction(req).onSuccess {
                loadData() // Reload data setelah berhasil tambah
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                FinanceViewModel(app.container.financeRepository)
            }
        }
    }
}