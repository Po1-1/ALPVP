package com.hendra.newalpvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.newalpvp.MomentumApplication
import com.hendra.newalpvp.data.repository.FinanceRepository
import com.hendra.newalpvp.ui.model.TransactionRequest
import com.hendra.newalpvp.ui.model.TransactionResponse
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
            // Format tanggal saat ini ke string (sesuai kebutuhan backend, biasanya ISO atau yyyy-MM-dd)
            val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(
                Date()
            )

            val req = TransactionRequest(type, amount, category, currentDate)
            repository.createTransaction(req).onSuccess {
                loadData() // PENTING: Refresh data setelah sukses tambah
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