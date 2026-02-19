package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountTaxScreen(navController: NavController) {
    var originalPrice by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("") }
    var finalAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discount & Tax Calculator") },
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
                value = originalPrice,
                onValueChange = { originalPrice = it },
                label = { Text("Original Price") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = discountPercent,
                onValueChange = { discountPercent = it },
                label = { Text("Discount (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = taxPercent,
                onValueChange = { taxPercent = it },
                label = { Text("Tax / GST (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                try {
                    val price = originalPrice.toDouble()
                    val disc = discountPercent.toDoubleOrNull() ?: 0.0
                    val tax = taxPercent.toDoubleOrNull() ?: 0.0

                    val afterDiscount = price * (1 - disc / 100)
                    val final = afterDiscount * (1 + tax / 100)

                    finalAmount = String.format(Locale.US, "Final Amount: ₹%.2f\nYou saved: ₹%.2f", final, price - afterDiscount)
                } catch (e: Exception) {
                    finalAmount = "Invalid input"
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Calculate")
            }

            if (finalAmount.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = finalAmount,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
