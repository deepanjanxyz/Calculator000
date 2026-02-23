package com.example.premiumcalculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.premiumcalculator.viewmodel.UnitConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericConverterDetailScreen(navController: NavController, category: String) {
    val viewModel: UnitConverterViewModel = hiltViewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    var value by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("") }
    var toUnit by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val units = getUnitsForCategory(category)
    if (fromUnit.isEmpty() && units.isNotEmpty()) fromUnit = units[0]
    if (toUnit.isEmpty() && units.size > 1) toUnit = units[1]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = value, onValueChange = { value = it; showError = false },
                label = { Text("Enter Value") }, isError = showError && value.isEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                UnitDropdown(units, fromUnit, "From", Modifier.weight(1f)) { fromUnit = it }
                Spacer(Modifier.width(12.dp))
                UnitDropdown(units, toUnit, "To", Modifier.weight(1f)) { toUnit = it }
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    val v = value.toDoubleOrNull()
                    if (v != null) {
                        val res = viewModel.convert(category, v, fromUnit, toUnit)
                        resultText = String.format("%.4f %s", res, toUnit)
                        showError = false; keyboardController?.hide()
                    } else { showError = true }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)
            ) { Text("Convert Unit", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

            if (resultText.isNotEmpty()) {
                Spacer(Modifier.height(40.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Result", style = MaterialTheme.typography.titleMedium)
                        Text(text = resultText, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(units: List<String>, selected: String, label: String, modifier: Modifier, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor(), shape = RoundedCornerShape(12.dp))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { u -> DropdownMenuItem(text = { Text(u) }, onClick = { onSelect(u); expanded = false }) }
        }
    }
}

private fun getUnitsForCategory(category: String): List<String> = when (category) {
    "Length" -> listOf("km", "m", "cm", "mm", "mile", "yard", "foot", "inch")
    "Area" -> listOf("sq km", "sq m", "acre", "hectare", "sq ft")
    "Volume" -> listOf("liter", "ml", "gallon", "m³")
    "Mass" -> listOf("kg", "g", "mg", "lb", "oz")
    "Temperature" -> listOf("C", "F", "K")
    "Time" -> listOf("second", "minute", "hour", "day")
    "Speed" -> listOf("km/h", "mph", "m/s")
    "Pressure" -> listOf("Pa", "kPa", "bar", "atm", "psi")
    "Energy" -> listOf("J", "kJ", "kcal", "BTU", "Wh")
    else -> emptyList()
}
