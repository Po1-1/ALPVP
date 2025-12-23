package com.hendra.alpvp.ui.view

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hendra.alpvp.data.service.AlarmSoundService
import com.hendra.alpvp.ui.theme.ALPVPTheme
import com.hendra.alpvp.ui.viewmodel.QuestState
import com.hendra.alpvp.ui.viewmodel.QuestViewModel

class QuestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showOnLockScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ALPVPTheme{
                QuestScreen(
                    onSuccess = {
                        // Matikan Service Suara
                        stopService(Intent(this, AlarmSoundService::class.java))
                        finish()
                    }
                )
            }
        }
    }

    private fun showOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}

// --- UI COMPOSABLE ---
@Composable
fun QuestScreen(
    onSuccess: () -> Unit,
    viewModel: QuestViewModel = viewModel(factory = QuestViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    // Blokir Tombol Back Fisik
    BackHandler {}

    Scaffold(
        containerColor = Color(0xFF1C1C1E) // Background Dark Grey
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP BAR (Back Button & Title)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Alarm",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 2. GREETING
            Text(
                text = state.greeting,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. BIG CLOCK
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = state.time,
                    color = Color.White,
                    fontSize = 80.sp, // Ukuran Font Besar sesuai desain
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = state.amPm,
                    color = Color.LightGray,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 14.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. WEATHER (Icon + Temp)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.weatherIconUrl != null) {
                    AsyncImage(
                        model = state.weatherIconUrl,
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Placeholder jika loading/error
                    Text("🌥️", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = state.temp,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Push content ke bawah

            // 5. MATH PROBLEM
            Text(
                text = "${state.num1} + ${state.num2} =",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 6. INPUT FIELD (Rounded Dark Grey)
            OutlinedTextField(
                value = state.userAnswer,
                onValueChange = { viewModel.onAnswerChange(it) },
                placeholder = { Text("Answer...", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(24.dp), // Rounded corner penuh
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E), // Card Dark
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            if (state.isError) {
                Text(
                    text = "Incorrect, try again!",
                    color = Color(0xFFEF5350), // Red Error
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. SUBMIT BUTTON (Pill Shape)
            Button(
                onClick = { viewModel.submitAnswer(onSuccess) },
                shape = RoundedCornerShape(50), // Pill Shape
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161)), // Grey Button
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Submit",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun QuestScreenPreview() {
    // Dummy State untuk Preview tanpa ViewModel
    val dummyState = QuestState(
        time = "12:30",
        amPm = "PM",
        greeting = "GOOD AFTERNOON",
        temp = "30°C",
        num1 = 25,
        num2 = 31,
        userAnswer = ""
    )
    ALPVPTheme{
        Scaffold(containerColor = Color(0xFF1C1C1E)) { padding ->
            // Copy paste sebagian kecil struktur QuestScreen manual untuk preview static
            // Karena kita tidak bisa inject ViewModel di preview, kita buat dummy UI
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.ChevronLeft, null, tint = Color.White)
                    Text(" Alarm", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(40.dp))
                Text("GOOD AFTERNOON", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(30.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("12:30", color = Color.White, fontSize = 80.sp)
                    Text("PM", color = Color.Gray, fontSize = 24.sp, modifier = Modifier.padding(bottom = 14.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("☀️", fontSize = 40.sp) // Dummy icon
                    Spacer(Modifier.width(12.dp))
                    Text("30°C", color = Color.White, fontSize = 32.sp)
                }
                Spacer(Modifier.weight(1f))
                Text("25 + 31 =", color = Color.White, fontSize = 32.sp)
                Spacer(Modifier.height(20.dp))
                // Mock Input
                Box(
                    modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF2C2C2E)),
                    contentAlignment = Alignment.CenterStart
                ) { Text(" Answer...", color = Color.Gray, modifier = Modifier.padding(start = 16.dp)) }
                Spacer(Modifier.height(24.dp))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161)), shape = RoundedCornerShape(50)) {
                    Text("Submit", color = Color.Black)
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}