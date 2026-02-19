package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeCalculatorScreen(navController: NavController) {
    var rawInput by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    var ageText by remember { mutableStateOf("") }

    // Auto-format the date as DD/MM/YYYY smoothly
    val formattedDate = buildString {
        for (i in rawInput.indices) {
            append(rawInput[i])
            if ((i == 1 || i == 3) && i != rawInput.lastIndex) {
                append("/")
            }
        }
    }

    // Real-time validation
    LaunchedEffect(rawInput) {
        if (rawInput.length >= 4) {
            val day = rawInput.take(2).toIntOrNull() ?: 0
            val month = rawInput.drop(2).take(2).toIntOrNull() ?: 0
            val year = if (rawInput.length == 8) rawInput.drop(4).take(4).toIntOrNull() ?: 0 else 0
            
            isValid = day in 1..31 && month in 1..12
            if (year > 0) {
                isValid = isValid && year in 1900..LocalDate.now().year
            }
        } else {
            isValid = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Age Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = { new ->
                    val digits = new.replace("[^0-9]".toRegex(), "")
                    if (digits.length <= 8) {
                        rawInput = digits
                    }
                },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                placeholder = { Text("25121995") },
                isError = !isValid,
                supportingText = { if (!isValid) Text("Invalid date format", color = MaterialTheme.colorScheme.error) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    IconButton(onClick = { /* Optional: Future Date Picker */ }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isValid && rawInput.length == 8) {
                        try {
                            val date = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            val today = LocalDate.now()
                            if (date.isAfter(today)) {
                                ageText = "Date cannot be in the future"
                                return@Button
                            }
                            val period = Period.between(date, today)
                            ageText = buildString {
                                append("${period.years} Years\n")
                                append("${period.months} Months, ${period.days} Days")
                            }
                        } catch (_: DateTimeParseException) {
                            ageText = "Invalid Date"
                        }
                    } else {
                        ageText = "Enter complete date (DD/MM/YYYY)"
                    }
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Calculate Age", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (ageText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = ageText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
