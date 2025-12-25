package com.example.calculator.storage



data class ReportPdfData(
    val calculation: CalculationPdfData,
    val client: ClientPdfData,
    val measurementPoint: MeasurementPdfData,
    val images: List<String>,
    val comment: String?,
    val tools: List<ToolsData>?,
    val reportDate: String,
    val calculationDate: String,
    val yourCompanyData: YourCompanyPdfData
)

data class CalculationPdfData(
    val company: String?,
    val temperature: String?,
    val relativeHumidity: String?,
    val atmosphericPressure: String?,
    val staticPressure: String?,
    val calibrationFactor: String?,
    val pressureDrop: String?,
    val density: String?,
    val flowRate: String?
)

data class ClientPdfData(
    val name: String?,
    val street: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val email: String?,
    val contactPersons: String?,
    val customerData: String?
)

data class MeasurementPdfData(
    val name: String?,
    val installationNumber: String?,
    val installationName: String?,
    val manufacturer: String?,
    val yearOfManufacture: String?,
    val serialNumber: String?,
    val note: String?
)

data class YourCompanyPdfData(
    val companyName: String?,
    val INN: String?,
    val initials: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val fax: String?,
    val email: String?,
    val website: String?,
    val imagePath: String?
)
