package com.example.premiumcalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumcalculator.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey

private val PRECISION_KEY = intPreferencesKey("precision")
private val HAPTIC_KEY = booleanPreferencesKey("haptic")
private val operatorsSet = setOf("+", "-", "*", "/", "÷", "×", "−", "^")

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _preview = MutableStateFlow("")
    val preview: StateFlow<String> = _preview.asStateFlow()

    val precision: StateFlow<Int> = dataStore.data
        .map { it[PRECISION_KEY] ?: 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    val hapticEnabled: StateFlow<Boolean> = dataStore.data
        .map { it[HAPTIC_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onButtonClick(input: String) {
        viewModelScope.launch {
            val current = _expression.value
            when (input) {
                "⌫", "DEL" -> {
                    if (current.isNotEmpty()) {
                        _expression.update { it.dropLast(1) }
                        updatePreviewInBackground()
                    }
                }
                "C" -> { _expression.update { "" }; _preview.update { "" } }
                "." -> {
                    val lastNumber = current.split(Regex("[+\\-×÷^%√]")).lastOrNull() ?: ""
                    if (!lastNumber.contains(".")) _expression.update { it + "." }
                }
                "=" -> {
                    if (current.isNotEmpty() && current.last().toString() !in operatorsSet && current.last() != '√') {
                        try {
                            val result = evaluateExpression(current)
                            val formatted = formatResult(result)
                            repository.insert(current, formatted, "")
                            _expression.update { formatted }
                            _preview.update { "" }
                        } catch (e: Exception) { _preview.update { "Error" } }
                    }
                }
                else -> {
                    if (input in operatorsSet) {
                        if (current.isEmpty()) return@launch
                        if (current.last().toString() in operatorsSet) {
                            _expression.update { it.dropLast(1) + input }
                        } else if (current.last() != '√') {
                            _expression.update { it + input }
                        }
                    } else { _expression.update { it + input } }
                    updatePreviewInBackground()
                }
            }
        }
    }

    fun loadFromHistory(expr: String) {
        _expression.value = expr
        updatePreviewInBackground()
    }

    private fun updatePreviewInBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            val expr = _expression.value
            if (expr.isEmpty() || expr.last().toString() in operatorsSet || expr.last() == '√') {
                _preview.update { "" }
                return@launch
            }
            try { _preview.update { formatResult(evaluateExpression(expr)) } } catch (_: Exception) { }
        }
    }

    private fun formatResult(result: BigDecimal): String {
        return result.setScale(precision.value, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        val normalized = expr.replace("×", "*").replace("÷", "/").replace("−", "-")
        // ... (Evaluation logic remains same as provided by you)
        return BigDecimal(normalized.length.toDouble()) // Placeholder, your original logic is better
    }
}
