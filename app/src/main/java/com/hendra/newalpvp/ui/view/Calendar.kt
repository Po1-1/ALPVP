package com.hendra.newalpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.newalpvp.ui.model.TodoResponse
import com.hendra.newalpvp.ui.theme.NEWALPVPTheme
import com.hendra.newalpvp.ui.viewmodel.TodoViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    viewModel: TodoViewModel = viewModel(factory = TodoViewModel.Factory)
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { DayOfWeek.values().toList() }
    
    // State untuk tanggal yang dipilih
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    
    // Ambil data todos dari ViewModel
    val todos by viewModel.todos.collectAsState()
    
    // Filter todos berdasarkan tanggal yang dipilih
    val selectedTodos = remember(selectedDate, todos) {
        if (selectedDate != null) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            todos.filter { 
                // Asumsi format time di TodoResponse dimulai dengan "yyyy-MM-dd"
                it.time.startsWith(selectedDate!!.format(formatter)) 
            }
        } else {
            emptyList()
        }
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        
        HorizontalCalendar(
            state = state,
            monthHeader = { month ->
                Column {
                    // Header Bulan dan Tahun
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        text = "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Header Hari (Senin, Selasa, dst)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (dayOfWeek in daysOfWeek) {
                            Text(
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }
                    }
                }
            },
            dayContent = { day ->
                // Cek apakah ada todo di hari ini
                val hasTodo = todos.any { 
                     // Simple check: apakah string waktu mengandung tanggal hari ini
                     // Format TodoResponse.time = "yyyy-MM-dd HH:mm"
                     val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                     it.time.startsWith(day.date.format(dateFormatter))
                }
                
                Day(day, isSelected = selectedDate == day.date, hasEvent = hasTodo) { clicked ->
                    selectedDate = clicked.date
                }
            }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Menampilkan Event/Todo untuk tanggal terpilih
        if (selectedDate != null) {
            Text(
                text = "Jadwal pada ${selectedDate}",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )
            
            if (selectedTodos.isEmpty()) {
                Text(
                    text = "Tidak ada jadwal.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(selectedTodos) { todo ->
                        ScheduleItem(todo)
                    }
                }
            }
        } else {
             Text(
                text = "Pilih tanggal untuk melihat jadwal.",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay, isSelected: Boolean, hasEvent: Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Membuat sel hari menjadi persegi (Grid)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        val textColor = when {
            isSelected -> Color.White
            day.position == DayPosition.MonthDate -> Color.Black
            else -> Color.LightGray // Tanggal dari bulan sebelum/sesudahnya
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            // Indikator Event (Dot merah jika ada todo)
            if (day.position == DayPosition.MonthDate && hasEvent) {
                 Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.Red)
                )
            }
        }
    }
}

@Composable
fun ScheduleItem(todo: TodoResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = todo.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = todo.time.substringAfter(" "), // Ambil jam-nya saja (HH:mm)
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    NEWALPVPTheme {
        // Kita tidak bisa preview ViewModel asli dengan mudah di sini, 
        // jadi preview UI dasar saja atau gunakan mock jika perlu.
        // CalendarView() 
    }
}
