package com.hendra.alpvp.ui.view

import androidx.compose.runtime.Composable
import com.hendra.alpvp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState){
        if (uiState is AuthUiState.Success) onRegisterSuccess()
    }

    RegisterScreenContent(
        uiState = uiState,

        onRegister = {name, email, pass -> viewModel.register(name, email, pass) },
        onNavigateToLogin = onNavigateToLogin
    )
}