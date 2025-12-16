package com.hendra.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.AlarmResponse
import com.hendra.alpvp.ui.viewmodel.SleepViewModel

private val BgDark = Color(0xFF1C1C1E)
private val CardDark = Color(0xFF2C2C2E)
private val TextWhite = Color.White
private val TextGray = Color.Gray
private val AccentPurple = Color(0xFF7C4DFF)
private val RedDelete = Color(0xFFCF6679)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    onBackClick: () -> Unit,
    viewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory)
){
    // FIX ERROR #1: Ganti 'alarm' jadi 'alarmList' dan 'currentAlarms' jadi 'alarmList'
    val alarmList by viewModel.alarms.collectAsState()
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(true) { viewModel.loadData() }

    SleepScreenContent(
        alarmList = alarmList, // FIX: Pakai 'alarmList' yang sudah didefinisikan
        onBackClick = onBackClick,
        onAddClick = { showBottomSheet = true },
        onToggleAlarm = { id, isActive -> viewModel.toggleAlarm(context, id, isActive) },
        onDeleteAlarm = { id -> viewModel.deleteAlarm(context, id) }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = CardDark,
            contentColor = TextWhite
        ) {
            AddAlarmSheetContent(
                onCancel = { showBottomSheet = false },
                onSave = { hour, minute, label, days ->
                    viewModel.addAlarm(context, hour, minute, label, days)
                    showBottomSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreenContent(
    alarmList: List<AlarmResponse>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onToggleAlarm: (String, Boolean) -> Unit,
    onDeleteAlarm: (String) -> Unit
){
    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Alarm", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDark),
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, "Add", tint = TextWhite)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()){
            if (alarmList.isEmpty()){
                Text(
                    text = "Belum ada alarm tersimpan",
                    color = TextGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(alarmList){ alarm ->
                        AlarmItemCard(
                            alarm = alarm,
                            onToggle = { isActive -> onToggleAlarm(alarm.id, isActive) },
                            onDelete = { onDeleteAlarm(alarm.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItemCard(
    alarm: AlarmResponse,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
){
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(20.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column {
                    Text(text = alarm.label, color = TextGray, fontSize = 14.sp)
                    // FIX ERROR #2: Langsung pakai alarm.time
                    Text(
                        text = alarm.time,
                        color = if (alarm.isActive) TextWhite else TextGray,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                Switch(
                    checked = alarm.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextWhite,
                        checkedTrackColor = AccentPurple,
                        uncheckedThumbColor = TextGray,
                        uncheckedTrackColor = BgDark
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ){
                // FIX ERROR #3: Fungsi helper untuk format hari
                Text(text = formatDaysString(alarm.days), color = TextGray, fontSize = 12.sp)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = RedDelete)
                }
            }
        }
    }
}

// FIX ERROR #3: Tambahkan fungsi helper
@Composable
fun formatDaysString(days: List<Boolean>): String {
    val daysName = listOf("M", "S", "S", "R", "K", "J", "S") // Minggu - Sabtu
    val activeDays = days.mapIndexed { i, active ->
        if (active) daysName[i] else null
    }.filterNotNull()

    return when {
        activeDays.isEmpty() -> "Sekali"
        activeDays.size == 7 -> "Setiap Hari"
        else -> activeDays.joinToString(" ")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmSheetContent(
    onCancel: () -> Unit,
    onSave: (Int, Int, String, List<Boolean>) -> Unit
){
    var label by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(List(7) { false })}
    val daysLabel = listOf("S", "M", "T", "W", "T", "F", "S")

    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 30, is24Hour = true)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier
            .width(40.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.Gray))
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Set Alarm",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tambahkan TimePicker yang hilang
        TimePicker(
            state = timeState,
            colors = TimePickerDefaults.colors(
                clockDialColor = BgDark,
                clockDialSelectedContentColor = TextWhite,
                clockDialUnselectedContentColor = TextGray,
                selectorColor = AccentPurple,
                timeSelectorSelectedContainerColor = AccentPurple,
                timeSelectorUnselectedContainerColor = BgDark,
                timeSelectorSelectedContentColor = TextWhite,
                timeSelectorUnselectedContentColor = TextGray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Ulangi Hari",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            daysLabel.forEachIndexed { index, dayName ->
                val isSelected = days[index]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AccentPurple else Color.Transparent)
                        .clickable{
                            val newDays = days.toMutableList()
                            newDays[index] = !newDays[index]
                            days = newDays
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = dayName,
                        color = if (isSelected) TextWhite else TextGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            placeholder = { Text("Label Alarm", color = TextGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgDark,
                unfocusedContainerColor = BgDark,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextButton(onClick = onCancel) {
                Text("Batal", color = TextWhite)
            }
            Button(
                onClick = {
                    val finalLabel = if(label.isEmpty()) "Alarm" else label
                    onSave(timeState.hour, timeState.minute, finalLabel, days)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TextWhite)
            ) {
                Text("Simpan", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun SleepScreenPreview() {
    val dummyAlarms = listOf(
        AlarmResponse("1", "06:00", "Bangun Pagi", List(7){true}, true, "u1"),
        AlarmResponse("2", "08:00", "Kerja", listOf(false, true, true, true, true, true, false), false, "u1"),
        AlarmResponse("3", "12:00", "Tidur Siang", List(7){false}, true, "u1")
    )

    SleepScreenContent(
        alarmList = dummyAlarms,
        onBackClick = {},
        onAddClick = {},
        onToggleAlarm = { _, _ -> },
        onDeleteAlarm = {}
    )

fun SleepScreenPreview(){
    SleepScreen()
}