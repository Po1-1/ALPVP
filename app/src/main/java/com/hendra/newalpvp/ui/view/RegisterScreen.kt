package com.hendra.newalpvp.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.newalpvp.ui.viewmodel.AuthUiState
import com.hendra.newalpvp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onRegisterSuccess()
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    uiState: AuthUiState,
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(containerColor = Color(0xFF1F1F1F)) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Register", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username, onValueChange = { username = it },
                placeholder = { Text("Username", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF2C2C2E), unfocusedContainerColor = Color(0xFF2C2C2E), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF2C2C2E), unfocusedContainerColor = Color(0xFF2C2C2E), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF2C2C2E), unfocusedContainerColor = Color(0xFF2C2C2E), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (uiState is AuthUiState.Loading) CircularProgressIndicator(color = Color.White)
            else Button(
                onClick = { onRegister(username, email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161))
            ) { Text("Submit", color = Color.White) }

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text("Sudah punya akun? ", color = Color.White)
                Text("Login", color = Color(0xFF64B5F6), modifier = Modifier.clickable { onNavigateToLogin() })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(AuthUiState.Idle, { _, _, _ -> }, {})
}