package com.example.premiumcalculator.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.CalculatorViewModel
import com.example.premiumcalculator.dataStore // DataStoreProvider থেকে ইমপোর্ট
import androidx.datastore.preferences.core.booleanPreferencesKey

// hapticKey যদি তোমার DataStoreProvider-এ না থাকে তবে এটি কাজ করবে
val hapticKey = booleanPreferencesKey("haptic_feedback")

data class CalcButton(val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    val hapticEnabled by context.dataStore.data.collectAsState(initial = null)
    
    val isHapticOn = hapticEnabled?.get(hapticKey) ?: true
    val expression by viewModel.expression
    val preview by viewModel.preview

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Tier: Pro Dashboard
        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(proTools) { tool ->
                    ProToolCard(tool, navController)
                }
            }
        }

        // Bottom Tier: Quick Calc
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = expression,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = preview,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(modifier = Modifier.height(250.dp)) { // হাইট ফিক্সড রাখা হয়েছে
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(basicButtons) { button ->
                            QuickCalcButton(
                                text = button.text,
                                onClick = {
                                    if (isHapticOn) {
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
    }
}

private val proTools = listOf(
    ProTool(Icons.Default.HealthAndSafety, "BMI/Health", "Track health", "bmi"),
    ProTool(Icons.Default.AttachMoney, "Investment", "Compound interest", "investment"),
    ProTool(Icons.Default.LocalGasStation, "Fuel Cost", "Optimize trips", "fuel"),
    ProTool(Icons.Default.CompareArrows, "Unit Price", "Compare deals", "unit_price"),
    ProTool(Icons.Default.School, "GPA/CGPA", "Academic tracker", "gpa"),
    ProTool(Icons.Default.CurrencyExchange, "Currency", "Real-time rates", "currency"),
    ProTool(Icons.Default.Cake, "Age", "Precise age", "age"),
    ProTool(Icons.Default.Calculate, "EMI", "Loan planning", "emi"),
    ProTool(Icons.Default.Functions, "Equation", "Solver", "solver")
)

data class ProTool(val icon: ImageVector, val title: String, val subtitle: String, val route: String)

@Composable
private fun ProToolCard(tool: ProTool, navController: NavController) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                    onTap = { navController.navigate(tool.route) }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(tool.icon, contentDescription = tool.title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(tool.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(tool.subtitle, fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}

private val basicButtons = listOf(
    CalcButton("7"), CalcButton("8"), CalcButton("9"), CalcButton("÷"),
    CalcButton("4"), CalcButton("5"), CalcButton("6"), CalcButton("×"),
    CalcButton("1"), CalcButton("2"), CalcButton("3"), CalcButton("−"),
    CalcButton("0"), CalcButton("."), CalcButton("="), CalcButton("+"),
    CalcButton("C"), CalcButton("DEL")
)

@Composable
private fun QuickCalcButton(text: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1.2f)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { pressed = true; tryAwaitRelease(); pressed = false })
            },
        shape = RoundedCornerShape(12.dp),
        color = if (text in listOf("÷", "×", "−", "+", "=")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
