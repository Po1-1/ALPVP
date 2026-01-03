package com.hendra.alpvp.ui.view

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.EventRequest
import com.hendra.alpvp.ui.model.EventResponse
import com.hendra.alpvp.ui.model.WebResponse
import com.hendra.alpvp.ui.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventDetailScreen(
    date: LocalDate,
    onClose: () -> Unit,
    onAddEvent: (LocalDate) -> Unit, // Kept to match signature, though unused locally
    viewModel: EventViewModel = viewModel(factory = EventViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    val eventsState by viewModel.eventsState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Process events from state
    val events = remember(eventsState, date) {
        val state = eventsState
        if (state is Result<*>) {
            try {
                @Suppress("UNCHECKED_CAST")
                val webResponse = state.getOrNull() as? WebResponse<List<EventResponse>>
                webResponse?.data?.filter { event ->
                    try {
                        val eventDateStr = event.date.take(10) // "yyyy-MM-dd"
                        LocalDate.parse(eventDateStr).isEqual(date)
                    } catch (_: Exception) {
                        false
                    }
                } ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        containerColor = Color(0xFF1F1F1F),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = Color(0xFF64B5F6),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${events.size} Events",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Event List
            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events for this day", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(events) { event ->
                        EventItem(event)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        AddEventBottomSheet(
            initialDate = date,
            onDismiss = { showBottomSheet = false },
            onSave = { dateStr, eventName, startTime, endTime ->
                val req = EventRequest(dateStr, eventName, startTime, endTime)
                viewModel.createEvent(req)
                viewModel.getAllEvents() // Refresh list
                showBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventBottomSheet(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var startTimeHour by remember { mutableIntStateOf(10) }
    var startTimeMinute by remember { mutableIntStateOf(0) }
    var endTimeHour by remember { mutableIntStateOf(11) }
    var endTimeMinute by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2C2C2E)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    "Add Event",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                val dateStr = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date", color = Color.White)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                selectedDate.year,
                                selectedDate.monthValue - 1,
                                selectedDate.dayOfMonth
                            )
                            datePickerDialog.show()
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    enabled = false
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Start Time", color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                TimePickerScrollable(
                    initialHour = startTimeHour,
                    initialMinute = startTimeMinute,
                    onTimeChange = { h, m ->
                        startTimeHour = h
                        startTimeMinute = m
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("End Time", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                TimePickerScrollable(
                    initialHour = endTimeHour,
                    initialMinute = endTimeMinute,
                    onTimeChange = { h, m ->
                        endTimeHour = h
                        endTimeMinute = m
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Button(
                    onClick = {
                        val startTimeStr = String.format(Locale.getDefault(), "%02d:%02d", startTimeHour, startTimeMinute)
                        val endTimeStr = String.format(Locale.getDefault(), "%02d:%02d", endTimeHour, endTimeMinute)
                        val dateStr = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        onSave(dateStr, eventName, startTimeStr, endTimeStr)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("Save Event", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerScrollable(
    initialHour: Int,
    initialMinute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    LaunchedEffect(state.hour, state.minute) {
        onTimeChange(state.hour, state.minute)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
         TimePicker(
            state = state,
            colors = TimePickerDefaults.colors(
                clockDialColor = Color(0xFF2C2C2E),
                selectorColor = Color(0xFF64B5F6),
                containerColor = Color(0xFF1F1F1F),
                clockDialSelectedContentColor = Color.White,
                clockDialUnselectedContentColor = Color.Gray,
                periodSelectorSelectedContainerColor = Color(0xFF64B5F6).copy(alpha = 0.5f),
                timeSelectorSelectedContainerColor = Color(0xFF2C2C2E),
                timeSelectorUnselectedContainerColor = Color(0xFF1F1F1F),
                timeSelectorSelectedContentColor = Color.White,
                timeSelectorUnselectedContentColor = Color.Gray
            )
        )
    }
}

@Composable
fun EventItem(event: EventResponse) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .width(60.dp)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = event.startTime,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
             Text(
                text = event.endTime,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray.copy(alpha = 0.7f)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF64B5F6).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF64B5F6),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
