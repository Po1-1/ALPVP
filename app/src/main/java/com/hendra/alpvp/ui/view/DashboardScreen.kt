package com.hendra.alpvp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(containerColor = Color(0xFF1F1F1F)) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Judul Dashboard
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tombol Menu Navigasi
            DashboardButton("Finance") { onNavigate("finance") }
            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton("To Do List") { onNavigate("todo") }
            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton("Alarm") { onNavigate("sleep") }
            Spacer(modifier = Modifier.height(16.dp))

            DashboardButton("Calendar") { onNavigate("calendar") }

            Spacer(modifier = Modifier.height(48.dp))

            // Tombol Logout
            TextButton(onClick = onLogout) {
                Text("Logout", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

// Komponen Tombol Dashboard Custom
@Composable
fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2E))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Preview Dashboard
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardScreen(onNavigate = {}, onLogout = {})
}
