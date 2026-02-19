package com.example.premiumcalculator.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.map

private val HAPTIC_KEY = booleanPreferencesKey("haptic")
private val Context.dataStore by preferencesDataStore(name = "settings")

data class CalcButton(val text: String)

private val buttons = listOf(
    CalcButton("7"), CalcButton("8"), CalcButton("9"), CalcButton("/"),
    CalcButton("4"), CalcButton("5"), CalcButton("6"), CalcButton("*"),
    CalcButton("1"), CalcButton("2"), CalcButton("3"), CalcButton("-"),
    CalcButton("0"), CalcButton("."), CalcButton("="), CalcButton("+"),
    CalcButton("("), CalcButton(")"), CalcButton("C"), CalcButton("DEL")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    val haptic by context.dataStore.data.map { it[HAPTIC_KEY] ?: true }.collectAsState(initial = true)
    val expression by viewModel.expression
    val preview by viewModel.preview

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.List, contentDescription = "History")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = preview,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.padding(8.dp)
            ) {
                items(buttons) { button ->
                    AnimatedButton(
                        text = button.text,
                        onClick = {
                            if (haptic) {
                                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            }
                            viewModel.onButtonClick(button.text)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedButton(text: String, onClick: () -> Unit) {
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed.value) 0.9f else 1f)

    Card(
        modifier = Modifier
            .padding(4.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed.value = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed.value = false
                        }
                        onClick()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text, style = MaterialTheme.typography.titleLarge)
        }
    }
}
