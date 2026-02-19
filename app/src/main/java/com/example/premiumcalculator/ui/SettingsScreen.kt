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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// DataStore initialization to save settings locally
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Read current values from DataStore
    var precision by remember { mutableIntStateOf(6) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var theme by remember { mutableStateOf("system") }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { prefs ->
            precision = prefs[intPreferencesKey("precision")] ?: 6
            hapticEnabled = prefs[booleanPreferencesKey("haptic")] ?: true
            theme = prefs[stringPreferencesKey("theme")] ?: "system"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // ──────────────────────────────
            // Precision (decimal places)
            // ──────────────────────────────
            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = precision.toFloat(),
                onValueChange = { new ->
                    precision = new.toInt()
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[intPreferencesKey("precision")] = precision
                        }
                    }
                },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
            Text("$precision decimal places", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // ──────────────────────────────
            // Haptic Feedback Toggle
            // ──────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Haptic Feedback", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Vibration on button press", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = hapticEnabled,
                    onCheckedChange = { enabled ->
                        hapticEnabled = enabled
                        scope.launch {
                            context.dataStore.edit { prefs ->
                                prefs[booleanPreferencesKey("haptic")] = enabled
                            }
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // ──────────────────────────────
            // Theme Selection
            // ──────────────────────────────
            Text("Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column {
                listOf("light" to "Light", "dark" to "Dark", "system" to "System Default").forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = theme == value,
                            onClick = {
                                theme = value
                                scope.launch {
                                    context.dataStore.edit { prefs ->
                                        prefs[stringPreferencesKey("theme")] = value
                                    }
                                }
                            }
                        )
                        Text(label, modifier = Modifier.padding(start = 12.dp), fontSize = 16.sp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // ──────────────────────────────
            // Clear History Button
            // ──────────────────────────────
            OutlinedButton(
                onClick = {
                    scope.launch {
                        Toast.makeText(context, "History Cleared Successfully!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear All History", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
