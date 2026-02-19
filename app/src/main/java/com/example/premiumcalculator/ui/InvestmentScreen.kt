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
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(navController: NavController) {
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Annually") }
    var result by remember { mutableStateOf("") }

    val frequencies = listOf("Annually", "Semi-Annually", "Quarterly", "Monthly")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investment Calculator") },
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
                value = principal,
                onValueChange = { principal = it },
                label = { Text("Principal Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it },
                label = { Text("Annual Rate (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (years)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = frequency,
                    onValueChange = { },
                    label = { Text("Compounding Frequency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    frequencies.forEach { freq ->
                        DropdownMenuItem(
                            text = { Text(freq) },
                            onClick = { 
                                frequency = freq
                                expanded = false 
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val p = principal.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val t = time.toDoubleOrNull() ?: 0.0
                    val n = when (frequency) {
                        "Semi-Annually" -> 2.0
                        "Quarterly" -> 4.0
                        "Monthly" -> 12.0
                        else -> 1.0
                    }
                    if (p > 0 && r > 0 && t > 0) {
                        val amount = p * (1 + r / (100 * n)).pow(n * t)
                        val interest = amount - p
                        result = "Maturity Amount: ${"%.2f".format(amount)}\nInterest Earned: ${"%.2f".format(interest)}"
                    } else {
                        result = "Invalid input"
                    }
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
