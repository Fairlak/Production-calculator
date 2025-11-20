package com.example.calculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.calculator.storage.ClientData
import com.example.calculator.storage.DecisionResult
import com.example.calculator.storage.InputData
import com.example.calculator.storage.MeasurementData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DbHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?):
    SQLiteOpenHelper(context, "app", factory, 4) {


    override fun onCreate(db: SQLiteDatabase?) {
        val historyQuery = """
            CREATE TABLE history (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                company TEXT, 
                tempCelsius REAL, 
                relativeHumidity REAL, 
                atmPressure REAL, 
                statPressure REAL, 
                calibrationFactor REAL, 
                pressureDrop REAL, 
                finalDensityValue REAL, 
                finalConsumptionValue REAL, 
                timeStamp TEXT
            )
        """.trimIndent()
        val clientsQuery = """
            CREATE TABLE clients (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                name TEXT, 
                street TEXT, 
                city TEXT,
                country TEXT,
                phoneNumber TEXT,
                eMail TEXT,
                contactPersons TEXT,
                customerData TEXT             
            )
        """.trimIndent()
        val measurementsQuery = """
            CREATE TABLE measurements (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                client_id INTEGER,
                pointName TEXT, 
                installationNumber TEXT, 
                installationName TEXT,
                manufacture TEXT,
                yearRelease TEXT,
                serialNumber TEXT,
                note TEXT          
            )
        """.trimIndent()
        db!!.execSQL(historyQuery)
        db.execSQL(clientsQuery)
        db.execSQL(measurementsQuery)




    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            val clientsQuery = """
                CREATE TABLE clients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    name TEXT, 
                    street TEXT, 
                    city TEXT,
                    country TEXT,
                    phoneNumber TEXT,
                    eMail TEXT,
                    contactPersons TEXT,
                    customerData TEXT          
                )
            """.trimIndent()
            val measurementsQuery = """
            CREATE TABLE measurements (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                pointName TEXT, 
                installationNumber TEXT, 
                installationName TEXT,
                manufacture TEXT,
                yearRelease TEXT,
                serialNumber TEXT,
                note TEXT          
            )
        """.trimIndent()
            db?.execSQL(clientsQuery)
            db?.execSQL(measurementsQuery)

            if (oldVersion < 4) {
                try {
                    db?.execSQL("ALTER TABLE measurements ADD COLUMN client_id INTEGER DEFAULT -1")
                } catch (e: Exception) {

                }
            }
        }
    }

    fun addClient(clientData: ClientData): Long {
        val values = ContentValues().apply {
            put("name", clientData.name)
            put("street", clientData.street)
            put("city", clientData.city)
            put("country", clientData.country)
            put("phoneNumber", clientData.phoneNumber)
            put("eMail", clientData.eMail)
            put("contactPersons", clientData.contactPersons)
            put("customerData", clientData.customerData)

        }
        return this.writableDatabase.use {
            it.insert("clients", null, values)
        }
    }

    fun addMeasurement(measurementData: MeasurementData, clientId: Long): Long {
        val values = ContentValues().apply {
            put("client_id", clientId)
            put("pointName", measurementData.pointName)
            put("installationNumber", measurementData.installationNumber)
            put("installationName", measurementData.installationName)
            put("manufacture", measurementData.manufacture)
            put("yearRelease", measurementData.yearRelease)
            put("serialNumber", measurementData.serialNumber)
            put("note", measurementData.note)

        }
        return this.writableDatabase.use {
            it.insert("measurements", null, values)
        }
    }

    fun addHistory(inputData: InputData, decisionResult: DecisionResult) {
        val values = ContentValues()
        values.put("company", decisionResult.company)
        values.put("tempCelsius", inputData.tempCelsius)
        values.put("relativeHumidity", inputData.relativeHumidity)
        values.put("atmPressure", inputData.atmPressure)
        values.put("statPressure", inputData.statPressure)
        values.put("calibrationFactor", inputData.calibrationFactor)
        values.put("pressureDrop", inputData.pressureDrop)
        values.put("finalDensityValue", decisionResult.finalDensityValue)
        values.put("finalConsumptionValue", decisionResult.finalConsumptionValue)
        values.put("timeStamp", decisionResult.timeStamp)
        this.writableDatabase.insert("history", null, values)
    }

    fun getAllHistory(): ArrayList<DecisionResult> {
        val historyList = ArrayList<DecisionResult>()
        this.readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM history ORDER BY id DESC", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex("id")
                        val companyIndex = cursor.getColumnIndex("company")
                        val densityIndex = cursor.getColumnIndex("finalDensityValue")
                        val consumptionIndex = cursor.getColumnIndex("finalConsumptionValue")
                        val timeStampIndex = cursor.getColumnIndex("timeStamp")

                        if (companyIndex != -1 && densityIndex != -1 && consumptionIndex != -1 && timeStampIndex != -1) {
                            val id = cursor.getLong(idIndex)
                            val company = cursor.getString(companyIndex)
                            val density = cursor.getDouble(densityIndex)
                            val consumption = cursor.getDouble(consumptionIndex)
                            val timeStamp = cursor.getString(timeStampIndex)

                            val entry = DecisionResult(
                                id = id,
                                finalDensityValue = density,
                                finalConsumptionValue = consumption,
                                company = company,
                                timeStamp = timeStamp
                            )
                            historyList.add(entry)
                        }
                    } while (cursor.moveToNext())
                }
            }
        }
        return historyList
    }

    fun getHistoryEntryById(entryId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM history WHERE id = ?", arrayOf(entryId.toString()))
    }


    fun getClientData(): ArrayList<ClientData> {
        val clientList = ArrayList<ClientData>()
        this.readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM clients ORDER BY id DESC", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex("id")
                        val nameIndex = cursor.getColumnIndex("name")
                        val streetIndex = cursor.getColumnIndex("street")
                        val cityIndex = cursor.getColumnIndex("city")
                        val countryIndex = cursor.getColumnIndex("country")
                        val phoneIndex = cursor.getColumnIndex("phoneNumber")
                        val eMailIndex = cursor.getColumnIndex("eMail")
                        val contactPersonsIndex = cursor.getColumnIndex("contactPersons")
                        val customerDataIndex = cursor.getColumnIndex("customerData")


                        if (nameIndex != -1 && streetIndex != -1 && cityIndex != -1 && countryIndex != -1 && eMailIndex != -1 && contactPersonsIndex != -1 && customerDataIndex != -1) {
                            val id = cursor.getLong(idIndex)
                            val name = cursor.getString(nameIndex)
                            val street = cursor.getString(streetIndex)
                            val city = cursor.getString(cityIndex)
                            val country = cursor.getString(countryIndex)
                            val phoneNumber = cursor.getString(phoneIndex)
                            val eMail = cursor.getString(eMailIndex)
                            val contactPersons = cursor.getString(contactPersonsIndex)
                            val customerData = cursor.getString(customerDataIndex)


                            val entry = ClientData(
                                id = id,
                                name = name,
                                street = street,
                                city = city,
                                country = country,
                                phoneNumber = phoneNumber,
                                eMail = eMail,
                                contactPersons = contactPersons,
                                customerData = customerData
                            )
                            clientList.add(entry)
                        }
                    } while (cursor.moveToNext())
                }
            }
        }
        return clientList
    }

    fun getClientDataEntryById(entryId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM clients WHERE id = ?", arrayOf(entryId.toString()))
    }

    fun updateClientData(id: Long, nameField: String, recordValue: String){
        val values = ContentValues().apply {
            put(nameField, recordValue)
        }
        this.writableDatabase.use { db ->
            db.update("clients", values, "id = ?",arrayOf(id.toString()))
        }

    }
    fun deleteClient(id: Long) {
        val db = this.writableDatabase
        db.delete("clients", "id = ?", arrayOf(id.toString()))
        db.close()
    }


    fun getMeasurementData(clientId: Long): ArrayList<MeasurementData> {
        val measurementList = ArrayList<MeasurementData>()
        this.readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM measurements WHERE client_id = ? ORDER BY id DESC", arrayOf(clientId.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex("id")
                        val pointNameIndex = cursor.getColumnIndex("pointName")
                        val installationNumberIndex = cursor.getColumnIndex("installationNumber")
                        val installationNameIndex = cursor.getColumnIndex("installationName")
                        val manufactureIndex = cursor.getColumnIndex("manufacture")
                        val yearReleaseIndex = cursor.getColumnIndex("yearRelease")
                        val serialNumberIndex = cursor.getColumnIndex("serialNumber")
                        val noteIndex = cursor.getColumnIndex("note")


                        if (pointNameIndex != -1 && installationNumberIndex != -1 && installationNameIndex != -1 && manufactureIndex != -1 && yearReleaseIndex != -1 && serialNumberIndex != -1 && noteIndex != -1) {
                            val id = cursor.getLong(idIndex)
                            val pointName = cursor.getString(pointNameIndex)
                            val installationNumber = cursor.getString(installationNumberIndex)
                            val installationName = cursor.getString(installationNameIndex)
                            val manufacture = cursor.getString(manufactureIndex)
                            val yearRelease = cursor.getString(yearReleaseIndex)
                            val serialNumber = cursor.getString(serialNumberIndex)
                            val note = cursor.getString(noteIndex)


                            val entry = MeasurementData(
                                id = id,
                                pointName = pointName,
                                installationNumber = installationNumber,
                                installationName = installationName,
                                manufacture = manufacture,
                                yearRelease = yearRelease,
                                serialNumber = serialNumber,
                                note = note
                            )
                            measurementList.add(entry)
                        }
                    } while (cursor.moveToNext())
                }
            }
        }
        return measurementList
    }

    fun getMeasurementEntryById(entryId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM measurements WHERE id = ?", arrayOf(entryId.toString()))
    }

    fun updateMeasurement(id: Long, nameField: String, recordValue: String){
        val values = ContentValues().apply {
            put(nameField, recordValue)
        }
        this.writableDatabase.use { db ->
            db.update("measurements", values, "id = ?",arrayOf(id.toString()))
        }
    }

    fun deleteMeasurement(id: Long) {
        val db = this.writableDatabase
        db.delete("measurements", "id = ?", arrayOf(id.toString()))
        db.close()
    }

}