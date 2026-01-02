package com.hendra.alpvp.ui.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.EventRequest
import com.hendra.alpvp.ui.model.EventResponse
import com.hendra.alpvp.ui.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    eventViewModel: EventViewModel = viewModel(factory = EventViewModel.Factory)
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EventResponse?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFF1F1F1F),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1F1F1F))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = Color.White,
                contentColor = Color(0xFF1F1F1F)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        val initialMonth = remember { YearMonth.from(LocalDate.now()) }
        val pageCount = Int.MAX_VALUE
        val initialPage = pageCount / 2

        val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pageCount })
        val scope = rememberCoroutineScope()

        // Fetch events whenever needed
        LaunchedEffect(Unit) {
            eventViewModel.getAllEvents()
        }

        val eventsState by eventViewModel.eventsState.collectAsState()

        val events = remember(eventsState, selectedDate) {
            eventViewModel.getEventsForDate(selectedDate)
        }

        Column(modifier = modifier.fillMaxSize().padding(padding)) {
            val displayedMonth = initialMonth.plusMonths((pagerState.currentPage - initialPage).toLong())

            // Calendar Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White)
                }
                Text(
                    text = "${displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${displayedMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
                }
            }

            // Day Headers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val daysOfWeek = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }

            // Calendar Grid
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) { page ->
                val month = initialMonth.plusMonths((page - initialPage).toLong())
                MonthView(
                    yearMonth = month,
                    selectedDate = selectedDate,
                    onDateClick = { date ->
                        selectedDate = date
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Events on ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Event List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                if (events.isEmpty()) {
                    item {
                        Text(
                            "No events for this date.",
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            onEdit = { eventToEdit = event },
                            onDelete = {
                                try {
                                    val id = event.id.toIntOrNull()
                                    if (id != null) {
                                        eventViewModel.deleteEvent(id)
                                        Toast.makeText(context, "Deleting event...", Toast.LENGTH_SHORT).show()
                                        eventViewModel.getAllEvents()
                                    } else {
                                        Toast.makeText(context, "Invalid Event ID", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error deleting", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        // Add Event Sheet
        if (showAddSheet) {
            EventFormBottomSheet(
                initialDate = selectedDate,
                onDismiss = { showAddSheet = false },
                onSave = { dateStr, eventName, startTime, endTime ->
                    val req = EventRequest(dateStr, eventName, startTime, endTime)
                    eventViewModel.createEvent(req)
                    eventViewModel.getAllEvents()
                    showAddSheet = false
                    Toast.makeText(context, "Event Created", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Edit Event Sheet
        if (eventToEdit != null) {
            val event = eventToEdit!!
            val eventDate = try { LocalDate.parse(event.date.take(10)) } catch (e:Exception) { LocalDate.now() }
            
            EventFormBottomSheet(
                initialDate = eventDate,
                eventToEdit = event,
                onDismiss = { eventToEdit = null },
                onSave = { dateStr, eventName, startTime, endTime ->
                    val id = event.id.toIntOrNull()
                    if (id != null) {
                        val req = EventRequest(dateStr, eventName, startTime, endTime)
                        eventViewModel.updateEvent(id, req)
                        eventViewModel.getAllEvents()
                        Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show()
                    }
                    eventToEdit = null
                }
            )
        }
    }
}

@Composable
fun EventCard(
    event: EventResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.eventName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "${event.startTime} - ${event.endTime}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64B5F6))
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun MonthView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7
    // Calculate total rows needed
    val totalCells = daysInMonth + startOffset
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val dayOfMonth = index - startOffset + 1
                    
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayOfMonth)
                        val isSelected = date.isEqual(selectedDate)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF64B5F6) else Color(0xFF4B4B4B))
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayOfMonth.toString(),
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormBottomSheet(
    initialDate: LocalDate,
    eventToEdit: EventResponse? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var eventName by remember { mutableStateOf(eventToEdit?.eventName ?: "") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    
    val (initStartH, initStartM) = try {
        if (eventToEdit != null) {
            val parts = eventToEdit.startTime.split(":")
            parts[0].toInt() to parts[1].toInt()
        } else 10 to 0
    } catch(e: Exception) { 10 to 0 }

    val (initEndH, initEndM) = try {
        if (eventToEdit != null) {
            val parts = eventToEdit.endTime.split(":")
            parts[0].toInt() to parts[1].toInt()
        } else 11 to 0
    } catch(e: Exception) { 11 to 0 }

    var startTimeHour by remember { mutableIntStateOf(initStartH) }
    var startTimeMinute by remember { mutableIntStateOf(initStartM) }
    var endTimeHour by remember { mutableIntStateOf(initEndH) }
    var endTimeMinute by remember { mutableIntStateOf(initEndM) }
    
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
                    text = if (eventToEdit == null) "Add Event" else "Edit Event",
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
                    label = { Text("Date", color = Color.Gray) },
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
                CalendarTimePicker(
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
                Text("End Time", color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                CalendarTimePicker(
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
                    Text(if (eventToEdit == null) "Save Event" else "Update Event", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTimePicker(
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
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1F1F1F), RoundedCornerShape(8.dp)).padding(8.dp),
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
