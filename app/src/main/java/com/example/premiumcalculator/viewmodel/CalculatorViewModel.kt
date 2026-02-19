package com.example.premiumcalculator.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumcalculator.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import java.util.ArrayDeque

private val PRECISION_KEY = intPreferencesKey("precision")

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _expression = mutableStateOf("")
    val expression: State<String> = _expression

    private val _preview = mutableStateOf("")
    val preview: State<String> = _preview

    private val _precisionFlow = dataStore.data
        .map { it[PRECISION_KEY] ?: 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    fun onButtonClick(text: String) {
        when (text) {
            "=" -> {
                try {
                    val result = evaluateExpression(_expression.value)
                    val formatted = formatResult(result)
                    _preview.value = formatted
                    viewModelScope.launch {
                        repository.insert(_expression.value, formatted)
                    }
                    _expression.value = formatted
                } catch (e: Exception) {
                    _preview.value = if (e.message == "Division by zero") "Cannot divide by zero" else "Error"
                }
            }
            "C" -> {
                _expression.value = ""
                _preview.value = ""
            }
            "DEL" -> {
                if (_expression.value.isNotEmpty()) {
                    _expression.value = _expression.value.dropLast(1)
                    updatePreview()
                }
            }
            else -> {
                _expression.value += text
                updatePreview()
            }
        }
    }

    private fun updatePreview() {
        _preview.value = try {
            formatResult(evaluateExpression(_expression.value))
        } catch (_: Exception) {
            ""
        }
    }

    private fun formatResult(result: BigDecimal): String {
        return result.setScale(_precisionFlow.value, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        if (expr.isEmpty()) return BigDecimal.ZERO
        val expression = expr.replace(" ", "")
        val tokens = mutableListOf<String>()
        var current = ""
        for (char in expression) {
            if (char.isDigit() || char == '.') {
                current += char
            } else {
                if (current.isNotEmpty()) {
                    tokens.add(current)
                    current = ""
                }
                tokens.add(char.toString())
            }
        }
        if (current.isNotEmpty()) tokens.add(current)

        val newTokens = mutableListOf<String>()
        for (i in tokens.indices) {
            val token = tokens[i]
            if (token == "-" && (i == 0 || "+-*/(".contains(tokens[i - 1]))) {
                newTokens.add("-1")
                newTokens.add("*")
            } else {
                newTokens.add(token)
            }
        }

        val output = ArrayDeque<BigDecimal>()
        val operators = ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in newTokens) {
            when {
                token.toBigDecimalOrNull() != null -> output.addLast(token.toBigDecimal())
                token == "(" -> operators.addLast(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.last() != "(") {
                        applyOperator(output, operators.removeLast())
                    }
                    if (operators.isNotEmpty() && operators.last() == "(") operators.removeLast()
                    else throw IllegalArgumentException("Mismatched parentheses")
                }
                precedence.containsKey(token) -> {
                    while (operators.isNotEmpty() && operators.last() != "(" && precedence[operators.last()]!! >= precedence[token]!!) {
                        applyOperator(output, operators.removeLast())
                    }
                    operators.addLast(token)
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        while (operators.isNotEmpty()) {
            if (operators.last() == "(") throw IllegalArgumentException("Mismatched parentheses")
            applyOperator(output, operators.removeLast())
        }

        if (output.size != 1) throw IllegalArgumentException("Invalid expression")
        return output.first()
    }

    private fun applyOperator(output: ArrayDeque<BigDecimal>, op: String) {
        if (output.size < 2) throw IllegalArgumentException("Invalid expression")
        val b = output.removeLast()
        val a = output.removeLast()
        val res = when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Division by zero") else a.divide(b, MathContext.DECIMAL128)
            else -> throw IllegalArgumentException("Invalid operator")
        }
        output.addLast(res)
    }
}
