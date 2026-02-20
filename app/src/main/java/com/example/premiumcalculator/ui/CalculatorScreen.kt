package com.example.premiumcalculator.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    val hapticEnabled by remember { mutableStateOf(true) }

    val expression by viewModel.expression.collectAsState()
    val preview by viewModel.preview.collectAsState()

    var showProTools by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showProTools) {
        ModalBottomSheet(
            onDismissRequest = { showProTools = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Pro Tools",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        scope.launch { sheetState.hide() }
                        showProTools = false
                    }) {
                        Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Divider()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(proTools) { tool ->
                        ProToolCard(tool, navController) {
                            scope.launch {
                                sheetState.hide()
                                showProTools = false
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calculator") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { showProTools = true }) {
                        Icon(Icons.Default.Widgets, contentDescription = "Pro Tools")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Display area - Scroll fixed
            Column(
                modifier = Modifier
                    .weight(2.8f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = preview.ifEmpty { "0" },
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                )
                Spacer(Modifier.height(48.dp))
            }

            // Keypad
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3.5f)
            ) {
                items(keypadButtons) { btn ->
                    KeypadButton(btn) {
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        if (vibrator?.hasVibrator() == true) {
                            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        }
                        viewModel.onButtonClick(btn.command)
                    }
                }
            }
        }
    }
}

private val keypadButtons = listOf(
    KeypadButton("7", "7"),
    KeypadButton("8", "8"),
    KeypadButton("9", "9"),
    KeypadButton("÷", "÷", isOperator = true),
    KeypadButton("4", "4"),
    KeypadButton("5", "5"),
    KeypadButton("6", "6"),
    KeypadButton("×", "×", isOperator = true),
    KeypadButton("1", "1"),
    KeypadButton("2", "2"),
    KeypadButton("3", "3"),
    KeypadButton("−", "−", isOperator = true),
    KeypadButton("0", "0"),
    KeypadButton(".", "."),
    KeypadButton("=", "=", isOperator = true),
    KeypadButton("+", "+", isOperator = true),
    KeypadButton("C", "C", special = true),
    KeypadButton("⌫", "⌫", special = true, useIcon = true)
)

data class KeypadButton(
    val display: String,
    val command: String,
    val isOperator: Boolean = false,
    val special: Boolean = false,
    val useIcon: Boolean = false
)

@Composable
private fun KeypadButton(btn: KeypadButton, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f)

    val container = when {
        btn.special -> MaterialTheme.colorScheme.errorContainer
        btn.isOperator -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val content = when {
        btn.special -> MaterialTheme.colorScheme.onErrorContainer
        btn.isOperator -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false }
                )
            },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        elevation = ButtonDefaults.buttonElevation(2.dp, pressedElevation = 0.dp)
    ) {
        if (btn.useIcon) {
            Icon(
                Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(btn.display, fontSize = 30.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private val proTools = listOf(
    ProTool(Icons.Default.Cake, "Age Calculator", "Exact age & live counter", "age"),
    ProTool(Icons.Default.Calculate, "EMI Calculator", "Loan & installment planner", "emi"),
    ProTool(Icons.Default.School, "GPA/CGPA", "Grade point average", "gpa"),
    ProTool(Icons.Default.CurrencyExchange, "Currency Converter", "Live rates", "currency"),
    ProTool(Icons.Default.LocalGasStation, "Fuel Cost", "Trip fuel calculator", "fuel"),
    ProTool(Icons.Default.HealthAndSafety, "BMI/Health", "Body mass index & BMR", "bmi"),
    ProTool(Icons.Default.CompareArrows, "Unit Price", "Best deal comparator", "unit_price"),
    ProTool(Icons.Default.AttachMoney, "Investment", "Compound interest", "investment"),
    ProTool(Icons.Default.ShoppingCart, "Discount/Tax", "Final price after tax/discount", "discount"),
    ProTool(Icons.Default.Landscape, "Land Converter", "Bigha / Kattha / Acre", "land"),
    ProTool(Icons.Default.Functions, "Equation Solver", "Linear & Quadratic", "solver")
)

data class ProTool(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String,
    val route: String
)

@Composable
private fun ProToolCard(tool: ProTool, navController: NavController, onDismiss: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f)

    Card(
        onClick = {
            navController.navigate(tool.route)
            onDismiss()
        },
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(tool.icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(tool.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(tool.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}
