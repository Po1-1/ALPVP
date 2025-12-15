package com.hendra.newalpvp.ui.routes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hendra.newalpvp.ui.container.TokenManager
import com.hendra.newalpvp.ui.view.DashboardScreen
import com.hendra.newalpvp.ui.view.FinanceScreen
import com.hendra.newalpvp.ui.view.LoginScreen
import com.hendra.newalpvp.ui.view.RegisterScreen
import com.hendra.newalpvp.ui.view.SleepScreen
import com.hendra.newalpvp.ui.view.TodoScreen

@Composable
fun MomentumApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- 1. LOGIKA CEK TOKEN (Menentukan Halaman Awal) ---
    val token = remember { TokenManager.getToken(context) }
    val startDestination = if (token != null) "home" else "login"

    // --- 2. LOGIKA IZIN NOTIFIKASI (Android 13+) ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Notifikasi dimatikan, alarm mungkin tidak muncul!", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // --- 3. NAVIGASI UTAMA ---
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            // Memanggil DashboardScreen dari file terpisah
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    TokenManager.clearToken(context)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("finance") {
            FinanceScreen(onBackClick = { navController.popBackStack() })
        }
        composable("todo") {
            TodoScreen(onBackClick = { navController.popBackStack() })
        }
        composable("sleep") {
            SleepScreen(onBackClick = { navController.popBackStack() })
        }
    }
}