package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var darkTheme by remember { mutableStateOf(false) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var decimalPlaces by remember { mutableStateOf(4) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            ListItem(
                headlineContent = { Text("Dark Theme") },
                trailingContent = {
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Haptic Feedback") },
                trailingContent = {
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { hapticEnabled = it }
                    )
                }
            )

            ListItem(
                headlineContent = { Text("Decimal Places") },
                supportingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 4, 6, 8).forEach { num ->
                            FilterChip(
                                selected = decimalPlaces == num,
                                onClick = { decimalPlaces = num },
                                label = { Text(num.toString()) }
                            )
                        }
                    }
                }
            )
        }
    }
}
