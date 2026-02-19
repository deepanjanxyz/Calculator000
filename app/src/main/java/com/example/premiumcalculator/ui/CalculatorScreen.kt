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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(navController: NavController) {
    val viewModel: CalculatorViewModel = hiltViewModel()
    val context = LocalContext.current
    val hapticEnabled by remember { mutableStateOf(true) }
    val expression by viewModel.expression
    val preview by viewModel.preview
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // Features Modal (Hidden by default)
    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Pro Tools & Features",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp, start = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxHeight(0.6f)
                ) {
                    items(proTools) { tool ->
                        ProToolCard(tool, navController) {
                            coroutineScope.launch {
                                sheetState.hide()
                                showMenu = false
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pro Calculator", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Widgets, contentDescription = "Features", tint = MaterialTheme.colorScheme.primary)
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
        ) {
            // Display Area (Now takes more space)
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = expression,
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = preview.ifEmpty { "0" },
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Keypad Area
            Box(modifier = Modifier.weight(2f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false
                ) {
                    items(basicButtons) { button ->
                        QuickCalcButton(
                            text = button.text,
                            onClick = {
                                if (hapticEnabled) {
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

private val proTools = listOf(
    ProTool(Icons.Default.HealthAndSafety, "BMI", "Health tracking", "bmi"),
    ProTool(Icons.Default.AttachMoney, "Finance", "Investment/EMI", "investment"),
    ProTool(Icons.Default.LocalGasStation, "Fuel", "Cost calculator", "fuel"),
    ProTool(Icons.Default.CompareArrows, "Pricing", "Unit comparator", "unit_price"),
    ProTool(Icons.Default.School, "GPA", "Grade tracker", "gpa"),
    ProTool(Icons.Default.CurrencyExchange, "Currency", "Exchange rates", "currency"),
    ProTool(Icons.Default.Settings, "Control Center", "App settings", "settings")
)

data class ProTool(val icon: ImageVector, val title: String, val subtitle: String, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProToolCard(tool: ProTool, navController: NavController, onDismiss: () -> Unit) {
    Card(
        onClick = {
            navController.navigate(tool.route)
            onDismiss()
        },
        modifier = Modifier.aspectRatio(1.1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(tool.icon, contentDescription = tool.title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(tool.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(tool.subtitle, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

private val basicButtons = listOf(
    CalcButton("C"), CalcButton("DEL"), CalcButton("%"), CalcButton("÷"),
    CalcButton("7"), CalcButton("8"), CalcButton("9"), CalcButton("×"),
    CalcButton("4"), CalcButton("5"), CalcButton("6"), CalcButton("−"),
    CalcButton("1"), CalcButton("2"), CalcButton("3"), CalcButton("+"),
    CalcButton("."), CalcButton("0"), CalcButton("history"), CalcButton("=")
)

data class CalcButton(val text: String)

@Composable
private fun QuickCalcButton(text: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)

    Surface(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f).scale(scale).pointerInput(Unit) {
            detectTapGestures(onPress = { pressed = true; tryAwaitRelease(); pressed = false })
        },
        shape = CircleShape,
        color = when {
            text == "=" -> MaterialTheme.colorScheme.primary
            text in listOf("÷", "×", "−", "+") -> MaterialTheme.colorScheme.secondaryContainer
            text in listOf("C", "DEL") -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (text == "history") {
                Icon(Icons.Default.History, contentDescription = "History")
            } else {
                Text(
                    text = text,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (text) {
                        "=" -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}
