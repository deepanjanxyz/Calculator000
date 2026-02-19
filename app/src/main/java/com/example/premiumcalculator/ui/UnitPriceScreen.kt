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
fun UnitPriceScreen(navController: NavController) {
    var price1 by remember { mutableStateOf("") }
    var weight1 by remember { mutableStateOf("") }
    var price2 by remember { mutableStateOf("") }
    var weight2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Price Comparator") },
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
            Text("Product 1", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = price1,
                onValueChange = { price1 = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = weight1,
                onValueChange = { weight1 = it },
                label = { Text("Weight/Quantity") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Product 2", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = price2,
                onValueChange = { price2 = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = weight2,
                onValueChange = { weight2 = it },
                label = { Text("Weight/Quantity") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val p1 = price1.toDoubleOrNull() ?: 0.0
                    val w1 = weight1.toDoubleOrNull() ?: 0.0
                    val p2 = price2.toDoubleOrNull() ?: 0.0
                    val w2 = weight2.toDoubleOrNull() ?: 0.0
                    if (p1 > 0 && w1 > 0 && p2 > 0 && w2 > 0) {
                        val unit1 = p1 / w1
                        val unit2 = p2 / w2
                        result = if (unit1 < unit2) "Product 1 is better deal\n(Unit: ${"%.2f".format(unit1)} vs ${"%.2f".format(unit2)})"
                        else if (unit1 > unit2) "Product 2 is better deal\n(Unit: ${"%.2f".format(unit2)} vs ${"%.2f".format(unit1)})"
                        else "Both are the same deal"
                    } else {
                        result = "Invalid input"
                    }
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compare", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
