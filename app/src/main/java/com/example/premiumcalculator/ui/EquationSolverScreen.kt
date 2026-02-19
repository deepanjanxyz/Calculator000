package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.EquationSolverViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquationSolverScreen(navController: NavController) {
    val viewModel: EquationSolverViewModel = hiltViewModel()
    var equation by remember { mutableStateOf("") }
    val result by viewModel.result

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equation Solver") },
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
                value = equation,
                onValueChange = { equation = it },
                label = { Text("Equation (ex: 2x² + 5x - 3 = 0)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.solve(equation) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solve")
            }

            if (result.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = result,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
