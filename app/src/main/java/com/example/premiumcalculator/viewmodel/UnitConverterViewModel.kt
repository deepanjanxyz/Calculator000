package com.example.premiumcalculator.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {

    fun convert(category: String, value: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return value
        return when (category) {
            "Length" -> lengthConvert(value, fromUnit, toUnit)
            "Area" -> areaConvert(value, fromUnit, toUnit)
            "Volume" -> volumeConvert(value, fromUnit, toUnit)
            "Mass" -> massConvert(value, fromUnit, toUnit)
            "Temperature" -> temperatureConvert(value, fromUnit, toUnit)
            "Time" -> timeConvert(value, fromUnit, toUnit)
            "Speed" -> speedConvert(value, fromUnit, toUnit)
            "Pressure" -> pressureConvert(value, fromUnit, toUnit)
            "Energy" -> energyConvert(value, fromUnit, toUnit)
            else -> value
        }
    }

    private fun lengthConvert(value: Double, from: String, to: String): Double {
        val toMeter = when (from.lowercase()) {
            "km" -> value * 1000; "cm" -> value / 100; "mm" -> value / 1000
            "mile" -> value * 1609.34; "yard" -> value * 0.9144; "foot" -> value * 0.3048
            "inch" -> value * 0.0254; else -> value
        }
        return when (to.lowercase()) {
            "km" -> toMeter / 1000; "cm" -> toMeter * 100; "mm" -> toMeter * 1000
            "mile" -> toMeter / 1609.34; "yard" -> toMeter / 0.9144; "foot" -> toMeter / 0.3048
            "inch" -> toMeter / 0.0254; else -> toMeter
        }
    }

    private fun areaConvert(value: Double, from: String, to: String): Double {
        val toSqm = when (from.lowercase()) {
            "sq km", "km²" -> value * 1_000_000; "sq cm", "cm²" -> value / 10_000
            "acre" -> value * 4046.86; "hectare" -> value * 10_000
            "sq ft", "ft²" -> value * 0.092903; else -> value
        }
        return when (to.lowercase()) {
            "sq km", "km²" -> toSqm / 1_000_000; "sq cm", "cm²" -> toSqm * 10_000
            "acre" -> toSqm / 4046.86; "hectare" -> toSqm / 10_000
            "sq ft", "ft²" -> toSqm / 0.092903; else -> toSqm
        }
    }

    private fun volumeConvert(value: Double, from: String, to: String): Double {
        val toLiter = when (from.lowercase()) {
            "ml" -> value / 1000; "gallon", "gal" -> value * 3.78541
            "cup" -> value * 0.236588; "m³" -> value * 1000; else -> value
        }
        return when (to.lowercase()) {
            "ml" -> toLiter * 1000; "gallon", "gal" -> toLiter / 3.78541
            "cup" -> toLiter / 0.236588; "m³" -> toLiter / 1000; else -> toLiter
        }
    }

    private fun massConvert(value: Double, from: String, to: String): Double {
        val toKg = when (from.lowercase()) {
            "g" -> value / 1000; "mg" -> value / 1_000_000
            "lb", "pound" -> value * 0.453592; "oz", "ounce" -> value * 0.0283495; else -> value
        }
        return when (to.lowercase()) {
            "g" -> toKg * 1000; "mg" -> toKg * 1_000_000
            "lb", "pound" -> toKg / 0.453592; "oz", "ounce" -> toKg / 0.0283495; else -> toKg
        }
    }

    private fun temperatureConvert(value: Double, from: String, to: String): Double {
        val c = when (from.uppercase()) {
            "F", "FAHRENHEIT" -> (value - 32) * 5 / 9; "K", "KELVIN" -> value - 273.15; else -> value
        }
        return when (to.uppercase()) {
            "F", "FAHRENHEIT" -> c * 9 / 5 + 32; "K", "KELVIN" -> c + 273.15; else -> c
        }
    }

    private fun timeConvert(value: Double, from: String, to: String): Double {
        val toSec = when (from.lowercase()) {
            "minute", "min" -> value * 60; "hour", "hr" -> value * 3600; "day" -> value * 86400; else -> value
        }
        return when (to.lowercase()) {
            "minute", "min" -> toSec / 60; "hour", "hr" -> toSec / 3600; "day" -> toSec / 86400; else -> toSec
        }
    }

    private fun speedConvert(value: Double, from: String, to: String): Double {
        val toMps = when (from.lowercase()) {
            "km/h", "kmh" -> value / 3.6; "mph" -> value * 0.44704; else -> value
        }
        return when (to.lowercase()) {
            "km/h", "kmh" -> toMps * 3.6; "mph" -> toMps / 0.44704; else -> toMps
        }
    }

    private fun pressureConvert(value: Double, from: String, to: String): Double {
        val toPa = when (from.lowercase()) {
            "kpa" -> value * 1000; "bar" -> value * 100_000; "atm" -> value * 101325; "psi" -> value * 6894.76; else -> value
        }
        return when (to.lowercase()) {
            "kpa" -> toPa / 1000; "bar" -> toPa / 100_000; "atm" -> toPa / 101325; "psi" -> toPa / 6894.76; else -> toPa
        }
    }

    private fun energyConvert(value: Double, from: String, to: String): Double {
        val toJ = when (from.lowercase()) {
            "kj" -> value * 1000; "kcal" -> value * 4184; "btu" -> value * 1055.06; "wh" -> value * 3600; else -> value
        }
        return when (to.lowercase()) {
            "kj" -> toJ / 1000; "kcal" -> toJ / 4184; "btu" -> toJ / 1055.06; "wh" -> toJ / 3600; else -> toJ
        }
    }
}
