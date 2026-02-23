package com.example.premiumcalculator.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val historyViewModel: HistoryViewModel = hiltViewModel()

    val precisionKey = intPreferencesKey("precision")
    val hapticKey = booleanPreferencesKey("haptic")
    val themeKey = stringPreferencesKey("theme")
    val colorKey = stringPreferencesKey("theme_color")

    val precision by context.dataStore.data.map { it[precisionKey] ?: 6 }.collectAsState(initial = 6)
    val hapticEnabled by context.dataStore.data.map { it[hapticKey] ?: true }.collectAsState(initial = true)
    val theme by context.dataStore.data.map { it[themeKey] ?: "system" }.collectAsState(initial = "system")
    val currentColorHex by context.dataStore.data.map { it[colorKey] ?: "#BB86FC" }.collectAsState(initial = "#BB86FC")

    var showClearDialog by remember { mutableStateOf(false) }

    val presetColors = listOf("#BB86FC", "#03DAC6", "#F44336", "#E91E63", "#2196F3", "#4CAF50", "#FFC107", "#FF5722", "#607D8B")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            
            Text("App Theme Color", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            
            // কালার গ্রিড
            LazyVerticalGrid(columns = GridCells.Fixed(5), modifier = Modifier.height(120.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetColors) { hex ->
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(hex))).clickable {
                        scope.launch { context.dataStore.edit { it[colorKey] = hex } }
                    }, contentAlignment = Alignment.Center) {
                        if (currentColorHex == hex) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium)
            Slider(value = precision.toFloat(), onValueChange = { scope.launch { context.dataStore.edit { prefs -> prefs[precisionKey] = it.toInt() } } }, valueRange = 0f..10f, steps = 9)
            Text("Current: $precision")

            Spacer(Modifier.height(24.dp))
            ListItem(headlineContent = { Text("Haptic Feedback") }, trailingContent = {
                Switch(checked = hapticEnabled, onCheckedChange = { isChecked -> scope.launch { context.dataStore.edit { it[hapticKey] = isChecked } } })
            })

            Spacer(Modifier.height(32.dp))
            Button(onClick = { showClearDialog = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Clear All History", fontSize = 16.sp)
            }
            
            // ... (Clear History Dialog Logic remains same)
        }
    }
}
