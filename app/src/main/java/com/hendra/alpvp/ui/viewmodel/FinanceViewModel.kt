package com.hendra.alpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.FinanceRepository
import com.hendra.alpvp.ui.model.TransactionRequest
import com.hendra.alpvp.ui.model.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions = _transactions.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.getTransactions().onSuccess { response ->
                _transactions.value = response.data
            }
        }
    }

    fun createTransaction(type: String, amount: Double, category: String) {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(
                Date()
            )

            val req = TransactionRequest(type, amount, category, currentDate)
            repository.createTransaction(req).onSuccess {
                loadData()
            }
        }
    }


}