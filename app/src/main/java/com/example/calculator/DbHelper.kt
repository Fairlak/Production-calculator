package com.example.calculator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.calculator.storage.DecisionResult
import com.example.calculator.storage.InputData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?): SQLiteOpenHelper(context, "app", factory, 1) {


    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE history (id INT PRIMARY KEY, company TEXT, tempCelsius REAL, relativeHumidity REAL, atmPressure REAL, statPressure REAL, calibrationFactor REAL, pressureDrop REAL, finalDensityValue REAL, finalConsumptionValue REAL, timeStamp TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS history")
        onCreate(db)
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
            db.rawQuery("SELECT * FROM history", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val companyIndex = cursor.getColumnIndex("company")
                        val densityIndex = cursor.getColumnIndex("finalDensityValue")
                        val consumptionIndex = cursor.getColumnIndex("finalConsumptionValue")
                        val timeStampIndex = cursor.getColumnIndex("timeStamp")

                        if (companyIndex != -1 && densityIndex != -1 && consumptionIndex != -1 && timeStampIndex != -1) {

                            val company = cursor.getString(companyIndex)
                            val density = cursor.getDouble(densityIndex)
                            val consumption = cursor.getDouble(consumptionIndex)
                            val timeStamp = cursor.getString(timeStampIndex)

                            val entry = DecisionResult(
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

}