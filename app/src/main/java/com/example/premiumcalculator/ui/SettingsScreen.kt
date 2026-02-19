package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.premiumcalculator.AppModule

private val THEME_KEY = stringPreferencesKey("theme")
private val HAPTIC_KEY = booleanPreferencesKey("haptic")
private val PRECISION_KEY = intPreferencesKey("precision")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme by context.dataStore.data.map { it[THEME_KEY] ?: "light" }.collectAsState(initial = "light")
    val haptic by context.dataStore.data.map { it[HAPTIC_KEY] ?: true }.collectAsState(initial = true)
    val precision by context.dataStore.data.map { it[PRECISION_KEY] ?: 6 }.collectAsState(initial = 6)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Theme", style = androidx.compose.material3.Typography().titleMedium)
            Row {
                RadioButton(selected = theme == "light", onClick = { scope.launch { context.dataStore.edit { it[THEME_KEY] = "light" } } })
                Text("Light")
            }
            Row {
                RadioButton(selected = theme == "dark", onClick = { scope.launch { context.dataStore.edit { it[THEME_KEY] = "dark" } } })
                Text("Dark")
            }
            Row {
                RadioButton(selected = theme == "black", onClick = { scope.launch { context.dataStore.edit { it[THEME_KEY] = "black" } } })
                Text("AMOLED Black")
            }

            Text("Haptic Feedback", style = androidx.compose.material3.Typography().titleMedium, modifier = Modifier.padding(top = 16.dp))
            Switch(checked = haptic, onCheckedChange = { value -> scope.launch { context.dataStore.edit { it[HAPTIC_KEY] = value } } })

            Text("Decimal Places", style = androidx.compose.material3.Typography().titleMedium, modifier = Modifier.padding(top = 16.dp))
            Row {
                RadioButton(selected = precision == 2, onClick = { scope.launch { context.dataStore.edit { it[PRECISION_KEY] = 2 } } })
                Text("2")
                RadioButton(selected = precision == 4, onClick = { scope.launch { context.dataStore.edit { it[PRECISION_KEY] = 4 } } })
                Text("4")
                RadioButton(selected = precision == 6, onClick = { scope.launch { context.dataStore.edit { it[PRECISION_KEY] = 6 } } })
                Text("6")
            }
        }
    }
}

private val android.content.Context.dataStore: DataStore<Preferences>
    get() = AppModule.provideDataStore(this)
