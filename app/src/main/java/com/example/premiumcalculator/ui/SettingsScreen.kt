package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.*
import com.example.premiumcalculator.dataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val theme by context.dataStore.data.map { it[themeKey] ?: "system" }.collectAsState(initial = "system")
    val primaryColor by context.dataStore.data.map { it[primaryColorKey] ?: "" }.collectAsState(initial = "")
    val fontScale by context.dataStore.data.map { it[fontScaleKey] ?: 1.0f }.collectAsState(initial = 1.0f)
    val precision by context.dataStore.data.map { it[precisionKey] ?: 8 }.collectAsState(initial = 8)
    val scientificNotation by context.dataStore.data.map { it[scientificNotationKey] ?: false }.collectAsState(initial = false)
    val degreeRadian by context.dataStore.data.map { it[degreeRadianKey] ?: "degree" }.collectAsState(initial = "degree")
    val hapticIntensity by context.dataStore.data.map { it[hapticIntensityKey] ?: 1.0f }.collectAsState(initial = 1.0f)
    val soundEffects by context.dataStore.data.map { it[soundEffectsKey] ?: false }.collectAsState(initial = false)
    val historyLimit by context.dataStore.data.map { it[historyLimitKey] ?: 100 }.collectAsState(initial = 100)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control Center") },
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
            // Appearance Section
            Text("Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Theme") },
                    supportingContent = {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            listOf("Light", "Dark", "System").forEachIndexed { index, option ->
                                SegmentedButton(
                                    selected = theme == option.lowercase(),
                                    onClick = { scope.launch { context.dataStore.edit { it[themeKey] = option.lowercase() } } },
                                    label = { Text(option) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                                )
                            }
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Font Scaling") },
                    trailingContent = {
                        Slider(
                            value = fontScale,
                            onValueChange = { newValue -> scope.launch { context.dataStore.edit { it[fontScaleKey] = newValue } } },
                            valueRange = 0.8f..1.5f,
                            steps = 6,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculations Section
            Text("Calculations", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Precision (Decimals)") },
                    trailingContent = {
                        Slider(
                            value = precision.toFloat(),
                            onValueChange = { newValue -> scope.launch { context.dataStore.edit { it[precisionKey] = newValue.toInt() } } },
                            valueRange = 2f..16f,
                            steps = 14,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                )
                ListItem(
                    headlineContent = { Text("Scientific Notation") },
                    trailingContent = {
                        Switch(
                            checked = scientificNotation,
                            onCheckedChange = { newValue -> scope.launch { context.dataStore.edit { it[scientificNotationKey] = newValue } } }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feedback Section
            Text("Feedback", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Haptic Intensity") },
                    trailingContent = {
                        Slider(
                            value = hapticIntensity,
                            onValueChange = { newValue -> scope.launch { context.dataStore.edit { it[hapticIntensityKey] = newValue } } },
                            valueRange = 0f..1f,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                )
                ListItem(
                    headlineContent = { Text("Sound Effects") },
                    trailingContent = {
                        Switch(
                            checked = soundEffects,
                            onCheckedChange = { newValue -> scope.launch { context.dataStore.edit { it[soundEffectsKey] = newValue } } }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data Section
            Text("Data", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("History Limit") },
                    trailingContent = {
                        OutlinedTextField(
                            value = historyLimit.toString(),
                            onValueChange = { newLimit -> scope.launch { context.dataStore.edit { it[historyLimitKey] = newLimit.toIntOrNull() ?: 100 } } },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                )
                ListItem(
                    headlineContent = { Text("Cache & History") },
                    supportingContent = {
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = { /* Export CSV */ }) { Text("CSV") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { /* Export PDF */ }) { Text("PDF") }
                        }
                    }
                )
            }
        }
    }
}

// Preference keys
private val themeKey = stringPreferencesKey("theme")
private val primaryColorKey = stringPreferencesKey("primary_color")
private val fontScaleKey = floatPreferencesKey("font_scale")
private val precisionKey = intPreferencesKey("precision")
private val scientificNotationKey = booleanPreferencesKey("scientific_notation")
private val degreeRadianKey = stringPreferencesKey("angle_mode")
private val hapticIntensityKey = floatPreferencesKey("haptic_intensity")
private val soundEffectsKey = booleanPreferencesKey("sound_effects")
private val historyLimitKey = intPreferencesKey("history_limit")
