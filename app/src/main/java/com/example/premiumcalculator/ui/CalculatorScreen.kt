package com.example.premiumcalculator.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    val expression by viewModel.expression.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calculator") },
                actions = {
                    PlainTooltipBox(tooltip = { Text("History") }) {
                        IconButton(onClick = { navController.navigate("history") }, modifier = Modifier.tooltipAnchor()) {
                            Icon(Icons.Default.History, "History")
                        }
                    }
                    PlainTooltipBox(tooltip = { Text("Settings") }) {
                        IconButton(onClick = { navController.navigate("settings") }, modifier = Modifier.tooltipAnchor()) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                    // ... (Other icons similar way)
                }
            )
        }
    ) { /* Keypad click logic updated to check hapticEnabled */
        // ... (Inside KeypadButton onClick)
        if (hapticEnabled) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
        viewModel.onButtonClick(btn.command)
    }
}
