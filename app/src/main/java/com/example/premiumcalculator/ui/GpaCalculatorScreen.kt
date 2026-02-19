package com.example.premiumcalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
fun GpaCalculatorScreen(navController: NavController) {
    var subjectsInput by remember { mutableStateOf("4") }
    val subjectsCount = subjectsInput.toIntOrNull() ?: 0
    val grades = remember { mutableStateListOf<String>().apply { repeat(10) { add("") } } }
    val credits = remember { mutableStateListOf<String>().apply { repeat(10) { add("") } } }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GPA/CGPA Calculator") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = subjectsInput,
                onValueChange = { subjectsInput = it },
                label = { Text("Number of Subjects (Max 10)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            repeat(subjectsCount.coerceAtMost(10)) { index ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (index < grades.size) grades[index] else "",
                        onValueChange = { if (index < grades.size) grades[index] = it },
                        label = { Text("Grade ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = if (index < credits.size) credits[index] else "",
                        onValueChange = { if (index < credits.size) credits[index] = it },
                        label = { Text("Credits ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = {
                    var totalPoints = 0.0
                    var totalCredits = 0.0
                    var hasError = false
                    
                    for (i in 0 until subjectsCount.coerceAtMost(10)) {
                        val g = grades[i].toDoubleOrNull()
                        val c = credits[i].toDoubleOrNull()
                        if (g != null && c != null && c > 0) {
                            totalPoints += g * c
                            totalCredits += c
                        } else {
                            hasError = true
                        }
                    }
                    
                    result = if (!hasError && totalCredits > 0) {
                        "GPA: ${"%.2f".format(totalPoints / totalCredits)}"
                    } else {
                        "Please enter valid Grades and Credits"
                    }
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate GPA", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
