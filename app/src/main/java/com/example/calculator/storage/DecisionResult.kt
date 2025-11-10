package com.example.calculator.storage

data class DecisionResult(
    val id: Long = 0,
    val finalDensityValue: Double,
    val finalConsumptionValue: Double,
    val company: String,
    val timeStamp: String
)