package com.hendra.alpvp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.newalpvp.MomentumApplication
import com.hendra.newalpvp.data.repository.AuthRepository
import com.hendra.newalpvp.ui.model.LoginRequest
import com.hendra.newalpvp.ui.model.RegisterRequest
import com.hendra.newalpvp.ui.container.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel {
}