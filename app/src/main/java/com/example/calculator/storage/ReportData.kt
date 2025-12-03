package com.example.calculator.storage

data class ReportData(
    val id: Long = 0,
    val reportTime: String = "",
    val clientId: Long = -1L,
    val measurementId: Long = -1L,
    val comment: String = "",
    val historyId: Long = -1L
)
