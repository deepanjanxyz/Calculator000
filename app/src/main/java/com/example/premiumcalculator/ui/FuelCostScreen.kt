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
fun FuelCostScreen(navController: NavController) {
    var distance by remember { mutableStateOf("") }
    var fuelPrice by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fuel Cost Calculator") },
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
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (km)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it },
                label = { Text("Fuel Price (per liter)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Mileage (km/liter)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val d = distance.toDoubleOrNull() ?: 0.0
                    val p = fuelPrice.toDoubleOrNull() ?: 0.0
                    val m = mileage.toDoubleOrNull() ?: 0.0
                    if (d > 0 && p > 0 && m > 0) {
                        val fuelNeeded = d / m
                        val cost = fuelNeeded * p
                        result = "Fuel Needed: ${"%.2f".format(fuelNeeded)} liters\nTotal Cost: ${"%.2f".format(cost)}"
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
