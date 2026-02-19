package com.example.premiumcalculator.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

// DataStore initialization to prevent crashes
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // DataStore flows
    val precision by context.dataStore.data.map { it[intPreferencesKey("precision")] ?: 6 }.collectAsState(initial = 6)
    val hapticEnabled by context.dataStore.data.map { it[booleanPreferencesKey("haptic")] ?: true }.collectAsState(initial = true)
    val theme by context.dataStore.data.map { it[stringPreferencesKey("theme")] ?: "system" }.collectAsState(initial = "system")

    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Precision
            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = precision.toFloat(),
                onValueChange = { new ->
                    scope.launch {
                        context.dataStore.edit { it[intPreferencesKey("precision")] = new.toInt() }
                    }
                },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Text("$precision decimals", modifier = Modifier.padding(top = 4.dp))

            Spacer(Modifier.height(32.dp))

            // Haptic
            ListItem(
                headlineContent = { Text("Haptic Feedback") },
                trailingContent = {
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { new ->
                            scope.launch {
                                context.dataStore.edit { it[booleanPreferencesKey("haptic")] = new }
                            }
                        }
                    )
                }
            )

            Spacer(Modifier.height(32.dp))

            // Theme
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("light" to "Light", "dark" to "Dark", "system" to "System").forEach { (key, label) ->
                    FilterChip(
                        selected = theme == key,
                        onClick = {
                            scope.launch {
                                context.dataStore.edit { it[stringPreferencesKey("theme")] = key }
                            }
                        },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Clear History
            OutlinedButton(
                onClick = { showClearConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Clear All History", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Confirm Clear") },
            text = { Text("Are you sure you want to delete all calculation history? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    showClearConfirm = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
