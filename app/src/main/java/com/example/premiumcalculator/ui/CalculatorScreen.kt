package com.example.premiumcalculator.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.CalculatorViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import com.example.premiumcalculator.dataStore

private val HAPTIC_KEY = booleanPreferencesKey("haptic")
private val BUTTON_ROUND_KEY = booleanPreferencesKey("button_round")
private val GLASSMORPHISM_KEY = booleanPreferencesKey("glassmorphism")

data class CalcButton(val text: String)

private val basicButtons = listOf(
    CalcButton("7"), CalcButton("8"), CalcButton("9"), CalcButton("/"),
    CalcButton("4"), CalcButton("5"), CalcButton("6"), CalcButton("*"),
    CalcButton("1"), CalcButton("2"), CalcButton("3"), CalcButton("-"),
    CalcButton("0"), CalcButton("."), CalcButton("="), CalcButton("+"),
    CalcButton("("), CalcButton(")"), CalcButton("C"), CalcButton("DEL"), CalcButton("%"), CalcButton("^")
)

private val scientificButtons = basicButtons + listOf(
    CalcButton("sin"), CalcButton("cos"), CalcButton("tan"), CalcButton("log"), CalcButton("ln"),
    CalcButton("π"), CalcButton("e"), CalcButton("!"), CalcButton("√")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    
    // DataStore flow handling
    val haptic by context.dataStore.data.map { it[HAPTIC_KEY] ?: true }.collectAsState(initial = true)
    val glassmorphism by context.dataStore.data.map { it[GLASSMORPHISM_KEY] ?: false }.collectAsState(initial = false)
    val buttonRound by context.dataStore.data.map { it[BUTTON_ROUND_KEY] ?: true }.collectAsState(initial = true)
    
    val scientificMode = remember { mutableStateOf(false) }
    val buttons = if (scientificMode.value) scientificButtons else basicButtons
    val expression by viewModel.expression
    val preview by viewModel.preview

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (glassmorphism) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Sci-Mode")
            Switch(checked = scientificMode.value, onCheckedChange = { scientificMode.value = it })
            IconButton(onClick = { navController.navigate("settings") }) { Icon(Icons.Default.Settings, "Settings") }
            IconButton(onClick = { navController.navigate("history") }) { Icon(Icons.Default.Refresh, "History") }
            Button(onClick = { navController.navigate("age") }, contentPadding = PaddingValues(4.dp)) { Text("Age") }
            Button(onClick = { navController.navigate("land") }, contentPadding = PaddingValues(4.dp)) { Text("Land") }
            Button(onClick = { navController.navigate("emi") }, contentPadding = PaddingValues(4.dp)) { Text("EMI") }
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(text = expression, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(text = preview, style = MaterialTheme.typography.displayLarge, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
        }
        
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.padding(8.dp)) {
            items(buttons) { button ->
                AnimatedButton(
                    text = button.text,
                    shape = if (buttonRound) CircleShape else RoundedCornerShape(8.dp),
                    onClick = {
                        if (haptic) {
                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        }
                        viewModel.onButtonClick(button.text)
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedButton(text: String, shape: Shape, onClick: () -> Unit) {
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed.value) 0.9f else 1f)

    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed.value = true
                        tryAwaitRelease()
                        pressed.value = false
                        onClick()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text, style = MaterialTheme.typography.titleLarge)
        }
    }
}
