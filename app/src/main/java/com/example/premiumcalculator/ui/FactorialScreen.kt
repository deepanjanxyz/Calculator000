package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactorialScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isCalculating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Factorial Calculator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { if(it.length <= 6) input = it.filter { char -> char.isDigit() } },
                label = { Text("Enter a number (Unlimited Power 🚀)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val n = input.toIntOrNull() ?: 0
                    isCalculating = true
                    result = "Calculating... Please wait (App might hang!)"
                    
                    scope.launch {
                        val factResult = withContext(Dispatchers.Default) {
                            try {
                                var fact = BigInteger.ONE
                                for (i in 1..n) {
                                    fact = fact.multiply(BigInteger.valueOf(i.toLong()))
                                }
                                fact.toString()
                            } catch (e: OutOfMemoryError) {
                                "Phone blasted! 💥 Out of Memory."
                            } catch (e: Exception) {
                                "Error: ${e.message}"
                            }
                        }
                        result = factResult
                        isCalculating = false
                    }
                }, 
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCalculating
            ) { 
                Text(if (isCalculating) "Calculating..." else "Calculate !") 
            }
            
            if (result.isNotEmpty()) {
                Text("Result:\n$result", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
