package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val historyViewModel: HistoryViewModel = hiltViewModel()

    val precisionKey = intPreferencesKey("precision")
    val hapticKey = booleanPreferencesKey("haptic")
    val themeKey = stringPreferencesKey("theme")

    val precision by context.dataStore.data.map { it[precisionKey] ?: 6 }.collectAsState(initial = 6)
    val hapticEnabled by context.dataStore.data.map { it[hapticKey] ?: true }.collectAsState(initial = true)
    val theme by context.dataStore.data.map { it[themeKey] ?: "system" }.collectAsState(initial = "system")

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium)
            Slider(value = precision.toFloat(), onValueChange = { scope.launch { context.dataStore.edit { it[precisionKey] = it.toInt() } } }, valueRange = 0f..10f, steps = 9)
            Text("Current: $precision")
            Spacer(Modifier.height(24.dp))
            ListItem(headlineContent = { Text("Haptic Feedback") }, trailingContent = {
                Switch(checked = hapticEnabled, onCheckedChange = { scope.launch { context.dataStore.edit { prefs -> prefs[hapticKey] = it } } })
            })
            Spacer(Modifier.height(32.dp))
            Button(onClick = { showClearDialog = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Clear All History", fontSize = 16.sp)
            }

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text("Clear History?") },
                    text = { Text("This will delete all history entries forever.") },
                    confirmButton = {
                        TextButton(onClick = { 
                            historyViewModel.clearAll()
                            showClearDialog = false 
                        }) { Text("Confirm", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } }
                )
            }
        }
    }
}
