package com.hendra.alpvp.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.TodoResponse
import com.hendra.alpvp.ui.theme.ALPVPTheme
import com.hendra.alpvp.ui.viewmodel.TodoViewModel
import java.util.Calendar
import kotlin.text.format
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty

@Composable
fun TodoScreen(
    onBackClick: () -> Unit,
    viewModel: TodoViewModel = viewModel(factory = TodoViewModel.Factory)
) {
    val todos by viewModel.todos.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) { viewModel.loadTodos() }

    TodoScreenContent(
        todos = todos,
        onBackClick = onBackClick,
        onAddTodo = { title, date, time ->
            // Gabungkan Date & Time menjadi satu string untuk dikirim ke ViewModel
            // Format: "yyyy-MM-dd HH:mm"
            viewModel.addTodo(context, title, "$date $time")
        },
        onToggleTodo = { viewModel.toggleTodo(it) },
        onDeleteTodo = { viewModel.deleteTodo(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreenContent(
    todos: List<TodoResponse>,
    onBackClick: () -> Unit,
    onAddTodo: (String, String, String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // 1. Setup Date Picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Format: yyyy-MM-dd
            selectedDate = "$year-${month + 1}-$dayOfMonth"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // 2. Setup Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            // Format: HH:mm
            selectedTime = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        containerColor = Color(0xFF121212), // Dark Background
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("To Do List", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(
                        0xFF121212
                    )
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- CARD INPUT ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Input Judul
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Judul Tugas", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row untuk Tombol Date & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { datePickerDialog.show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (selectedDate.isEmpty()) "Pilih Tgl" else selectedDate,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { timePickerDialog.show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (selectedTime.isEmpty()) "Pilih Jam" else selectedTime,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol ADD
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                onAddTodo(title, selectedDate, selectedTime)
                                // Reset Form
                                title = ""
                                selectedDate = ""
                                selectedTime = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Tambah Tugas", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Daftar Tugas",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- LIST TUGAS ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggle = { onToggleTodo(todo.id) },
                        onDelete = { onDeleteTodo(todo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: TodoResponse,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF7C4DFF),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    color = if (todo.isCompleted) Color.Gray else Color.White,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = todo.time, // Menampilkan Waktu Lengkap
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350))
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    ALPVPTheme{
        // Data Dummy untuk Preview
        val dummyList = listOf(
            TodoResponse("1", "Mengerjakan ALP", "2023-12-25 10:00", true, false),
            TodoResponse("2", "Belanja Bulanan", "2023-12-26 15:30", true, true),
            TodoResponse("3", "Olahraga", "2023-12-27 06:00", true, false)
        )

        TodoScreenContent(
            todos = dummyList,
            onBackClick = {},
            onAddTodo = { _, _, _ -> },
            onToggleTodo = {},
            onDeleteTodo = {}
        )
    }
}