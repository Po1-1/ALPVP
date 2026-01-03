package com.hendra.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.alpvp.ui.model.TransactionResponse
import com.hendra.alpvp.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale

// --- COLORS ---
private val BgDark = Color(0xFF1F1F1F)
private val CardDark = Color(0xFF2C2C2E)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray = Color(0xFFAAAAAA)
private val GreenIncome = Color(0xFF66BB6A)
private val RedExpense = Color(0xFFEF5350)

@Composable
fun FinanceScreen(
    onBackClick: () -> Unit,
    viewModel: FinanceViewModel = viewModel(factory = FinanceViewModel.Factory)
) {
    // Ambil uiState secara keseluruhan
    val uiState by viewModel.uiState.collectAsState()

    // Gunakan uiState.transactions untuk mendapatkan listnya
    val transactions = uiState.transactions

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    FinanceScreenContent(
        transactions = transactions,
        onBackClick = onBackClick,
        onAddTransaction = { type, amount, category ->
            viewModel.createTransaction(type, amount, category)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreenContent(
    transactions: List<TransactionResponse>,
    onBackClick: () -> Unit,
    onAddTransaction: (String, Double, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Hitung Total Saldo
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance Flow", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = TextWhite,
                contentColor = BgDark
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // --- 1. SUMMARY CARD ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Balance", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatRupiah(balance),
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Income", color = TextWhite, fontSize = 14.sp)
                            Text(formatRupiah(totalIncome), color = GreenIncome, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Expenses", color = TextWhite, fontSize = 14.sp)
                            Text(formatRupiah(totalExpense), color = RedExpense, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text("Recent Transactions", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. LIST TRANSAKSI (DIBALIK AGAR TERBARU DI ATAS) ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // PERUBAHAN UTAMA DI SINI: .reversed()
                items(transactions.reversed()) { trx ->
                    TransactionItemCard(trx)
                }
            }
        }

        // --- DIALOG INPUT TRANSAKSI ---
        if (showDialog) {
            AddTransactionDialog(
                onDismiss = { showDialog = false },
                onSave = { type, amount, category ->
                    onAddTransaction(type, amount, category)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionItemCard(trx: TransactionResponse) {
    val isIncome = trx.type == "INCOME"
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Bulat
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isIncome) GreenIncome.copy(alpha = 0.2f) else RedExpense.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isIncome) GreenIncome else RedExpense,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                // Detail Text
                Column {
                    Text(trx.category, color = TextWhite, fontWeight = FontWeight.SemiBold)
                    // Menampilkan tanggal pendek (YYYY-MM-DD)
                    val displayDate = if (trx.date.length >= 10) trx.date.substring(0, 10) else trx.date
                    Text(displayDate, color = TextGray, fontSize = 12.sp)
                }
            }
            // Nominal
            Text(
                text = (if (isIncome) "+ " else "- ") + formatRupiah(trx.amount),
                color = if (isIncome) GreenIncome else RedExpense,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") } // Default Pengeluaran

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Tambah Transaksi", color = TextWhite) },
        text = {
            Column {
                // Pilihan Tipe (Income / Expense)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = { type = "INCOME" },
                        label = { Text("Pemasukan") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenIncome,
                            selectedLabelColor = TextWhite,
                            labelColor = TextGray,
                            containerColor = Color.Transparent
                        ),
                    )
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = { type = "EXPENSE" },
                        label = { Text("Pengeluaran") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedExpense,
                            selectedLabelColor = TextWhite,
                            labelColor = TextGray,
                            containerColor = Color.Transparent
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Nominal
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountStr = it },
                    label = { Text("Nominal (Rp)", color = TextGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextGray,
                        cursorColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Kategori
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (misal: Makan)", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextGray,
                        cursorColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (amountStr.isNotEmpty() && category.isNotEmpty()) {
                        onSave(type, amountStr.toDouble(), category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TextWhite)
            ) {
                Text("Simpan", color = BgDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextGray) }
        }
    )
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ").replace(",00", "")
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun FinanceScreenPreview() {
    FinanceScreenContent(
        transactions = listOf(
            TransactionResponse("1", "INCOME", 5000000.0, "Gaji Bulanan", "2023-10-01", "u1"),
            TransactionResponse("2", "EXPENSE", 50000.0, "Makan Siang", "2023-10-02", "u1"),
            TransactionResponse("3", "EXPENSE", 200000.0, "Bensin", "2023-10-03", "u1")
        ),
        onBackClick = {},
        onAddTransaction = { _, _, _ -> }
    )
}