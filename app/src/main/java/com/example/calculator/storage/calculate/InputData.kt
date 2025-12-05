package com.example.calculator.storage.calculate

data class InputData(
    val tempCelsius: Double,
    val relativeHumidity: Double,
    val atmPressure: Double,
    val statPressure: Double,
    val calibrationFactor: Double,
    val pressureDrop: Double
)