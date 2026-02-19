package com.example.premiumcalculator.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumcalculator.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.StateFlow

private val PRECISION_KEY = intPreferencesKey("precision")
private val operators = setOf("+", "−", "×", "÷")

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _expression = mutableStateOf("")
    val expression: State<String> = _expression

    private val _preview = mutableStateOf("")
    val preview: State<String> = _preview

    private val precision: StateFlow<Int> = dataStore.data
        .map { it[PRECISION_KEY] ?: 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    fun onButtonClick(text: String) {
        when (text) {
            "=" -> {
                if (_expression.value.isEmpty()) return
                try {
                    val result = evaluateExpression(_expression.value)
                    val formatted = formatResult(result)
                    viewModelScope.launch {
                        repository.insert(_expression.value, formatted, "")
                    }
                    _expression.value = formatted
                    _preview.value = ""
                } catch (e: Exception) {
                    _preview.value = if (e.message?.contains("zero") == true) "Cannot divide by zero" else "Error"
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
                val current = _expression.value
                if (text in operators) {
                    if (current.isEmpty()) {
                        if (text == "−") _expression.value = "−"
                        return
                    }
                    if (current.last().toString() in operators) {
                        _expression.value = current.dropLast(1) + text
                    } else {
                        _expression.value += text
                    }
                } else {
                    _expression.value += text
                }
                updatePreview()
            }
        }
    }

    private fun updatePreview() {
        if (_expression.value.isEmpty() || _expression.value.last().toString() in operators) {
            _preview.value = ""
            return
        }
        _preview.value = try {
            formatResult(evaluateExpression(_expression.value))
        } catch (_: Exception) {
            ""
        }
    }

    private fun formatResult(result: BigDecimal): String {
        val p = precision.value
        return result.setScale(p, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        val tokens = expr.replace("×", "*").replace("÷", "/").replace("−", "-")
        return try {
            val result = simpleEval(tokens)
            BigDecimal(result.toString())
        } catch (e: Exception) {
            throw e
        }
    }

    // A simple math evaluator for basic operations
    private fun simpleEval(expr: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expr.length) expr[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expr.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    } else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = expr.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }
}
