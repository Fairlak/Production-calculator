package com.example.calculator.pdf

import android.content.Context
import com.example.calculator.DbHelper
import com.example.calculator.storage.CalculationPdfData
import com.example.calculator.storage.ClientPdfData
import com.example.calculator.storage.MeasurementPdfData
import com.example.calculator.storage.ReportPdfData
import com.example.calculator.storage.ToolsData
import com.example.calculator.storage.YourCompanyPdfData


fun createReportPdfStorage(reportId: Long, images: List<String>, context: Context): ReportPdfData {
    val db = DbHelper(context, null)

    var historyId: Long = -1L
    var clientId: Long = -1L
    var measurementId: Long = -1L
    var comment: String = ""
    var reportTime: String = ""


    var name: String = ""
    var street: String = ""
    var city: String = ""
    var country: String = ""
    var phoneNumber: String = ""
    var eMail: String = ""
    var contactPersons: String = ""
    var customerData: String = ""


    var pointName: String = ""
    var installationNumber: String = ""
    var installationName: String = ""
    var manufacture: String = ""
    var yearRelease: String = ""
    var serialNumber: String = ""
    var note: String = ""


    var company: String = ""
    var tempCelsius: Double = 0.0
    var relativeHumidity: Double = 0.0
    var atmPressure: Double = 0.0
    var statPressure: Double = 0.0
    var calibrationFactor: Double = 0.0
    var pressureDrop: Double = 0.0
    var finalDensityValue: Double = 0.0
    var finalConsumptionValue: Double = 0.0
    var timeStamp: String = ""


    var yourCompanyName: String = ""
    var yourCompanyINN: String = ""
    var yourInitials: String = ""
    var yourAddress: String = ""
    var yourCity: String = ""
    var yourCountry: String = ""
    var yourPhoneNumber: String = ""
    var yourFax: String = ""
    var yourEMail: String = ""
    var yourWebsite: String = ""
    var imagePath: String = ""



    if (reportId != -1L) {
        db.getReportDataEntryById(reportId).use { cursor ->
            if (cursor.moveToFirst()) {
                historyId = cursor.getLong(cursor.getColumnIndexOrThrow("historyId"))
                clientId = cursor.getLong(cursor.getColumnIndexOrThrow("clientId"))
                measurementId = cursor.getLong(cursor.getColumnIndexOrThrow("measurementId"))
                comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))
                reportTime = cursor.getString(cursor.getColumnIndexOrThrow("reportTime"))
            }
        }
    }

    if (clientId != -1L) {
        db.getClientDataEntryById(clientId).use { cursor ->
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                street = cursor.getString(cursor.getColumnIndexOrThrow("street"))
                city = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                country = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
                eMail = cursor.getString(cursor.getColumnIndexOrThrow("eMail"))
                contactPersons = cursor.getString(cursor.getColumnIndexOrThrow("contactPersons"))
                customerData = cursor.getString(cursor.getColumnIndexOrThrow("customerData"))
            }
        }
    }

    if (measurementId != -1L) {
        db.getMeasurementEntryById(measurementId).use { cursor ->
            if (cursor.moveToFirst()) {
                pointName = cursor.getString(cursor.getColumnIndexOrThrow("pointName"))
                installationNumber = cursor.getString(cursor.getColumnIndexOrThrow("installationNumber"))
                installationName = cursor.getString(cursor.getColumnIndexOrThrow("installationName"))
                manufacture = cursor.getString(cursor.getColumnIndexOrThrow("manufacture"))
                yearRelease = cursor.getString(cursor.getColumnIndexOrThrow("yearRelease"))
                serialNumber = cursor.getString(cursor.getColumnIndexOrThrow("serialNumber"))
                note = cursor.getString(cursor.getColumnIndexOrThrow("note"))
            }
        }
    }

    if (historyId != -1L) {
        db.getHistoryEntryById(historyId).use { cursor ->
            if (cursor.moveToFirst()) {
                company = cursor.getString(cursor.getColumnIndexOrThrow("company"))
                tempCelsius = cursor.getDouble(cursor.getColumnIndexOrThrow("tempCelsius"))
                relativeHumidity = cursor.getDouble(cursor.getColumnIndexOrThrow("relativeHumidity"))
                atmPressure = cursor.getDouble(cursor.getColumnIndexOrThrow("atmPressure"))
                statPressure = cursor.getDouble(cursor.getColumnIndexOrThrow("statPressure"))
                calibrationFactor = cursor.getDouble(cursor.getColumnIndexOrThrow("calibrationFactor"))
                pressureDrop = cursor.getDouble(cursor.getColumnIndexOrThrow("pressureDrop"))
                finalDensityValue = cursor.getDouble(cursor.getColumnIndexOrThrow("finalDensityValue"))
                finalConsumptionValue = cursor.getDouble(cursor.getColumnIndexOrThrow("finalConsumptionValue"))
                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow("timeStamp"))
            }
        }
    }

    db.getYourCompanyData().use { cursor ->
        if (cursor.moveToFirst()) {
            yourCompanyName = cursor.getString(cursor.getColumnIndexOrThrow("yourCompanyName"))
            yourCompanyINN = cursor.getString(cursor.getColumnIndexOrThrow("INN"))
            yourInitials = cursor.getString(cursor.getColumnIndexOrThrow("yourInitials"))
            yourAddress = cursor.getString(cursor.getColumnIndexOrThrow("yourAddress"))
            yourCity = cursor.getString(cursor.getColumnIndexOrThrow("yourCity"))
            yourCity = cursor.getString(cursor.getColumnIndexOrThrow("yourCity"))
            yourCountry = cursor.getString(cursor.getColumnIndexOrThrow("yourCountry"))
            yourPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("yourPhoneNumber"))
            yourFax = cursor.getString(cursor.getColumnIndexOrThrow("yourFax"))
            yourEMail = cursor.getString(cursor.getColumnIndexOrThrow("yourEMail"))
            yourWebsite = cursor.getString(cursor.getColumnIndexOrThrow("yourWebsite"))
            imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"))
        }
    }

    val toolsList: ArrayList<ToolsData>? = if (reportId != -1L) {
        db.getToolsData(reportId)
    } else {
        null
    }


    val reportPdfData = ReportPdfData(
        calculation = CalculationPdfData(
            company = company,
            temperature = tempCelsius.toString(),
            relativeHumidity = relativeHumidity.toString(),
            atmosphericPressure = atmPressure.toString(),
            staticPressure = statPressure.toString(),
            calibrationFactor = calibrationFactor.toString(),
            pressureDrop = pressureDrop.toString(),
            density = "%.4f".format(finalDensityValue),
            flowRate = "%.2f".format(finalConsumptionValue)
        ),
        client = ClientPdfData(
            name = name,
            street = street,
            city = city,
            country = country,
            phone = phoneNumber,
            email = eMail,
            contactPersons = contactPersons,
            customerData = customerData
        ),
        measurementPoint = MeasurementPdfData(
            name = pointName,
            installationNumber = installationNumber,
            installationName = installationName,
            manufacturer = manufacture,
            yearOfManufacture = yearRelease,
            serialNumber = serialNumber,
            note = note
        ),
        yourCompanyData = YourCompanyPdfData(
            companyName = yourCompanyName,
            INN = yourCompanyINN,
            initials = yourInitials,
            address = yourAddress,
            city = yourCity,
            country = yourCountry,
            phone = yourPhoneNumber,
            fax = yourFax,
            email = yourEMail,
            website = yourWebsite,
            imagePath = imagePath
        ),
        images = images,
        comment = comment,
        reportDate = reportTime,
        calculationDate = timeStamp,
        tools = toolsList
    )

    return reportPdfData
}