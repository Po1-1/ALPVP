package com.hendra.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.EventResponse
import com.hendra.alpvp.ui.model.WebResponse
import com.hendra.alpvp.ui.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailScreen(
    date: LocalDate,
    onClose: () -> Unit,
    onAddEvent: (LocalDate) -> Unit,
    viewModel: EventViewModel = viewModel(factory = EventViewModel.Factory) 
) {
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    val eventsState by viewModel.eventsState.collectAsState()
    
    // Process events from state
    val events = remember(eventsState, date) {
        val state = eventsState
        if (state is Result<*>) {
            try {
                // If the state is a Result containing WebResponse of List of EventResponse
                val webResponse = state.getOrNull() as? WebResponse<List<EventResponse>>
                webResponse?.data?.filter { event ->
                    try {
                        // Assuming event.date format could be ISO or just date
                        val eventDateStr = event.date.take(10) // "yyyy-MM-dd"
                        // Parse logic can be improved based on actual API format
                        LocalDate.parse(eventDateStr).isEqual(date)
                    } catch (e: Exception) {
                        false
                    }
                } ?: emptyList()
            } catch (e: Exception) {
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
                onClick = { onAddEvent(date) },
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
}

@Composable
fun EventItem(event: EventResponse) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Time Column
        Column(
            modifier = Modifier
                .width(60.dp)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Try to extract time from date string if present, else "All Day"
            val timeText = try {
                if (event.date.length > 10) {
                    // Assume ISO format like "2023-10-27T10:00:00"
                    event.date.substring(11, 16)
                } else {
                    "All Day"
                }
            } catch (e: Exception) {
                "All Day"
            }
            
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // Event Card
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
                if (event.eventName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
