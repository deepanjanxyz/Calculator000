package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandConverterScreen(navController: NavController) {
    var inputValue by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("Decimal") }
    var toUnit by remember { mutableStateOf("Katha") }
    var result by remember { mutableStateOf("") }

    val units = listOf("Decimal", "Katha", "Bigha", "Acre", "Shotok")

    // Conversion factors (approx, Bangladesh standard)
    val toDecimal = mapOf(
        "Decimal" to 1.0,
        "Katha" to 1.0 / 1.653,
        "Bigha" to 1.0 / 33.057,
        "Acre" to 1.0 / 100.0,
        "Shotok" to 1.0
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Land Converter") },
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
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Value") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Note: ExposedDropdownMenuBox is handled here
                Box(modifier = Modifier.weight(1f)) {
                     TextField(
                        readOnly = true,
                        value = fromUnit,
                        onValueChange = { },
                        label = { Text("From") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    TextField(
                        readOnly = true,
                        value = toUnit,
                        onValueChange = { },
                        label = { Text("To") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(onClick = {
                try {
                    val value = inputValue.toDoubleOrNull() ?: 0.0
                    val decimal = value * (toDecimal[fromUnit] ?: 1.0)
                    val converted = decimal / (toDecimal[toUnit] ?: 1.0)
                    result = String.format(Locale.US, "%.4f %s", converted, toUnit)
                } catch (e: Exception) {
                    result = "Invalid input"
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Convert")
            }

            if (result.isNotBlank()) {
                Text(
                    text = result,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
