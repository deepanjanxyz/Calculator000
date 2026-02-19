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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiHealthScreen(navController: NavController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf(0.0) }
    var bmr by remember { mutableStateOf(0.0) }
    var isMale by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI/Health Tracker") },
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
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                RadioButton(selected = isMale, onClick = { isMale = true })
                Text("Male", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !isMale, onClick = { isMale = false })
                Text("Female", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val h = height.toDoubleOrNull() ?: 0.0
                    val a = age.toIntOrNull() ?: 0
                    if (w > 0 && h > 0 && a > 0) {
                        bmi = w / ((h / 100).pow(2))
                        bmr = if (isMale) 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
                        else 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
                        result = "BMI: ${"%.2f".format(bmi)}\nBMR: ${"%.2f".format(bmr)} kcal/day"
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
