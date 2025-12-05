package com.example.calculator.storage.clients

data class MeasurementData (
    val id: Long = 0,
    val pointName: String = "",
    val installationNumber: String = "",
    val installationName: String = "",
    val manufacture: String = "",
    val yearRelease: String = "",
    val serialNumber: String = "",
    val note: String = ""
)