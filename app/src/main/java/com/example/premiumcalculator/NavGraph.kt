package com.example.premiumcalculator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.premiumcalculator.ui.CalculatorScreen
import com.example.premiumcalculator.ui.HistoryScreen
import com.example.premiumcalculator.ui.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "calculator") {
        composable("calculator") {
            CalculatorScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("history") {
            HistoryScreen(navController)
        }
    }
}
