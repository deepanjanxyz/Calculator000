package com.example.premiumcalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf("") }

    // Mock rates (For offline/basic version)
    val rates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "INR" to 83.0,
        "JPY" to 150.0
    )
    val currencies = rates.keys.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency Converter") },
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
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .blur(10.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            var expandedFrom by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFrom,
                onExpandedChange = { expandedFrom = !expandedFrom }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = fromCurrency,
                    onValueChange = { },
                    label = { Text("From") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFrom,
                    onDismissRequest = { expandedFrom = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text(curr) },
                            onClick = { 
                                fromCurrency = curr
                                expandedFrom = false 
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            var expandedTo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedTo,
                onExpandedChange = { expandedTo = !expandedTo }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = toCurrency,
                    onValueChange = { },
                    label = { Text("To") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTo,
                    onDismissRequest = { expandedTo = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text(curr) },
                            onClick = { 
                                toCurrency = curr
                                expandedTo = false 
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    val fromRate = rates[fromCurrency] ?: 1.0
                    val toRate = rates[toCurrency] ?: 1.0
                    if (amt > 0) {
                        val converted = amt * (toRate / fromRate)
                        result = "${"%.2f".format(converted)} $toCurrency"
                    } else {
                        result = "Invalid amount"
                    }
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convert", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
