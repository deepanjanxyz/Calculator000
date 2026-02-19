package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeCalculatorScreen(navController: NavController) {
    var birthDateStr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Age Calculator") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = birthDateStr,
                onValueChange = { birthDateStr = it },
                label = { Text("Birth Date (YYYY-MM-DD)") },
                placeholder = { Text("1995-08-15") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    try {
                        val birth = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                        val today = LocalDate.now()
                        val period = Period.between(birth, today)
                        result = "${period.years} years, ${period.months} months, ${period.days} days"
                    } catch (e: DateTimeParseException) {
                        result = "Invalid date format"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Age")
            }

            if (result.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = result,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
