package com.hendra.alpvp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.container.TokenManager
import com.hendra.alpvp.data.repository.AuthRepository
import com.hendra.alpvp.ui.model.LoginRequest
import com.hendra.alpvp.ui.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val msg: String) : AuthUiState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, pass: String, context: Context) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(LoginRequest(email, pass)).fold(
                onSuccess = { response ->
                    // PERBAIKAN: Ambil token dari response.data.token
                    // it/response adalah WebResponse -> data adalah UserResponse -> token ada di sana
                    TokenManager.saveToken(context, response.data.token ?: "")
                    _uiState.value = AuthUiState.Success
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Login Gagal")
                }
            )
        }
    }

    fun register(username: String, email: String, pass: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.register(RegisterRequest(username, email, pass)).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Register Gagal")
                }
            )
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                AuthViewModel(app.container.authRepository)
            }
        }
    }
}