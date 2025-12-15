package com.hendra.newalpvp.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.newalpvp.ui.viewmodel.AuthUiState
import com.hendra.newalpvp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onLoginSuccess()
    }

    LoginScreenContent(
        uiState = uiState,
        onLogin = { email, pass -> viewModel.login(email, pass, context) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(containerColor = Color(0xFF1F1F1F)) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = { onLogin(email, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161))
                ) { Text("Login", color = Color.White) }
            }

            if (uiState is AuthUiState.Error) {
                Text(text = uiState.msg, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text("Belum punya akun? ", color = Color.White)
                Text("Register", color = Color(0xFF64B5F6), modifier = Modifier.clickable { onNavigateToRegister() })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(AuthUiState.Idle, { _, _ -> }, {})
}