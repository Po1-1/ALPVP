package com.hendra.alpvp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.alpvp.MomentumApplication
import com.hendra.alpvp.data.repository.AuthRepository
import com.hendra.alpvp.ui.model.LoginRequest
import com.hendra.alpvp.ui.model.RegisterRequest
import com.hendra.alpvp.data.container.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val msg: String) : AuthUiState
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, pass: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(LoginRequest(email, pass))
            result.onSuccess { response ->
                // Assuming token format needs "Bearer " prefix if not present, 
                // but usually backend sends it or we append it in interceptor.
                // TokenManager just saves the string.
                // Based on AppContainer interceptor: request.addHeader("Authorization", token)
                // If token from backend is just the JWT, we might need "Bearer $token".
                // If backend sends "Bearer ...", then just save it.
                // Let's assume we need to save what we get, or add Bearer if missing. 
                // However, usually it's safer to just save what is returned if it is a full token, 
                // or prepend Bearer if it's raw JWT.
                // Looking at typical implementations, let's prepend Bearer for now if it's not there, 
                // or just save it. The previous code snippet I wrote for AuthViewModel used "Bearer ${response.data.token}".
                TokenManager.saveToken(context, "Bearer ${response.data.token}")
                _uiState.value = AuthUiState.Success
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Login failed")
            }
        }
    }
    
    fun register(email: String, pass: String, username: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
             _uiState.value = AuthUiState.Loading
            val result = authRepository.register(RegisterRequest(email, pass, username))
            result.onSuccess {
                _uiState.value = AuthUiState.Success
                onResult(true)
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Register failed")
                onResult(false)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                val authRepository = application.container.authRepository
                AuthViewModel(authRepository)
            }
        }
    }
}
