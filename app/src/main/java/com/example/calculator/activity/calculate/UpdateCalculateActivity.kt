package com.example.calculator.activity.calculate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getDoubleOrNull
import com.example.calculator.DbHelper
import com.example.calculator.FormulaCalc
import com.example.calculator.R
import com.example.calculator.storage.calculate.DecisionResult
import com.example.calculator.storage.calculate.InputData
import com.google.android.material.textfield.TextInputLayout

class UpdateCalculateActivity : AppCompatActivity() {


    private lateinit var manufacturer: TextView
    private lateinit var formula: TextView
    private lateinit var result: TextView
    private lateinit var tempCelsius: TextInputLayout
    private lateinit var relativeHumidity: TextInputLayout
    private lateinit var atmPressure: TextInputLayout
    private lateinit var statPressure: TextInputLayout
    private lateinit var calibrationFactor: TextInputLayout
    private lateinit var pressureDrop: TextInputLayout
    private val db by lazy { DbHelper(this, null) }

    private var historyId: Long = -1L
    private var reportId: Long = -1L

    private var finalDensityValue: Double = 0.0
    private var finalConsumptionValue: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_calculate)

        val backButton: ImageButton = findViewById(R.id.calc_back_button)


        val openWarningDeleteButton: ImageButton = findViewById(R.id.open_warning_delete_calc_button)
        val deleteCalcView: View = findViewById(R.id.delete_calc_warning_layout)
        val overlayViewCalc: View = findViewById(R.id.overlay_view_calc)
        val deleteCalcButton: Button = findViewById(R.id.delete_calc_button)
        val updateCalcButton: Button = findViewById(R.id.update_calc_button)
        val deleteCancelCalcButton: Button = findViewById(R.id.delete_cancel_calc_button)




        reportId = intent.getLongExtra("ReportId", -1L)
        historyId = intent.getLongExtra("HistoryId", -1L)



        if (historyId != -1L) {
            db.getHistoryEntryById(historyId).use { calculateCursor ->
                if (calculateCursor.moveToFirst()) {
                    val companyDb =
                        calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("company"))
                    val inputData = createStorageInstance()
                    val processor = FormulaCalc(inputData)
                    finalDensityValue = processor.airDensityCalculation()
                    finalConsumptionValue = processor.consumption(companyDb)

                }

            }
        }

        openWarningDeleteButton.setOnClickListener {
            deleteCalcView.visibility = View.VISIBLE
            overlayViewCalc.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            finish()
        }

        deleteCalcButton.setOnClickListener {
            if (historyId != -1L) db.deleteHistory(historyId)

            if (reportId != -1L){
                db.updateReport(reportId, "historyId", -1L)

            }
            finish()
        }

        deleteCancelCalcButton.setOnClickListener {
            deleteCalcView.visibility = View.GONE
            overlayViewCalc.visibility = View.GONE
        }

        updateCalcButton.setOnClickListener {
            val inputData = createStorageInstance()
            val manufacturerName = manufacturer.text.toString()
            val processor = FormulaCalc(inputData)
            finalDensityValue = processor.airDensityCalculation()
            finalConsumptionValue = processor.consumption(manufacturerName)
            saveAllFields()
            updateData()

        }

        updateData()
    }

    fun createStorageInstance(): InputData {
        tempCelsius = findViewById<TextInputLayout>(R.id.update_temp)
        relativeHumidity = findViewById<TextInputLayout>(R.id.update_wet)
        atmPressure = findViewById<TextInputLayout>(R.id.update_atm_pressure)
        statPressure = findViewById<TextInputLayout>(R.id.update_stat_pressure)
        calibrationFactor = findViewById<TextInputLayout>(R.id.update_calibration_factor)
        pressureDrop = findViewById<TextInputLayout>(R.id.update_pressure_drop)



        val tempStr = tempCelsius.editText?.text.toString()
        val humStr = relativeHumidity.editText?.text.toString()
        val atmStr = atmPressure.editText?.text.toString()
        val statStr = statPressure.editText?.text.toString()
        val calibStr = calibrationFactor.editText?.text.toString()
        val dropStr = pressureDrop.editText?.text.toString()



        var temp = tempStr.toDoubleOrNull() ?: 0.0
        var hum = humStr.toDoubleOrNull() ?: 0.0
        var atm = atmStr.toDoubleOrNull() ?: 0.0
        var stat = statStr.toDoubleOrNull() ?: 0.0
        var calib = calibStr.toDoubleOrNull() ?: 0.0
        var drop = dropStr.toDoubleOrNull() ?: 0.0

        when {
            hum > 100.0 -> hum = 100.0
            hum < 0.0 -> hum = 0.0

            atm > 1500.0 -> atm = 1500.0
            atm < -1500.0 -> atm = -1500.0

            temp < -273.15 -> temp = -273.15
            temp > 1000 -> temp = 1000.0

            stat < -10000.0 -> stat = -10000.0
            stat > 10000.0 -> stat = 10000.0

            calib < -10000.0 -> calib = -10000.0
            calib > 10000.0 -> calib = 10000.0

            drop < -10000.0 -> drop = -10000.0
            drop > 10000.0 -> drop = 10000.0


        }

        return InputData(temp, hum, atm, stat, calib, drop)
    }



    fun updateData(){
        if (historyId != -1L) {

            result = findViewById<TextView>(R.id.update_itog)
            manufacturer = findViewById<TextView>(R.id.update_manufacturer)
            formula = findViewById<TextView>(R.id.update_copied_formula)

            tempCelsius = findViewById<TextInputLayout>(R.id.update_temp)
            relativeHumidity = findViewById<TextInputLayout>(R.id.update_wet)
            atmPressure = findViewById<TextInputLayout>(R.id.update_atm_pressure)
            statPressure = findViewById<TextInputLayout>(R.id.update_stat_pressure)
            calibrationFactor = findViewById<TextInputLayout>(R.id.update_calibration_factor)
            pressureDrop = findViewById<TextInputLayout>(R.id.update_pressure_drop)


            val formuls = mapOf(
                "FläktWoods" to "q = (1/k) * √ΔP",
                "Rosenberg" to "q = k * √(2 * ΔP / ρ)",
                "Nicotra-Gebhardt" to "q = k * √(2 * ΔP / ρ)",
                "Comefri" to "q = k * √(2 * ΔP / ρ)",
                "Ziehl" to "q = k * √ΔP",
                "ebm-papst" to "q = k * √ΔP",
                "Gebhardt" to "q = k * √(2 * ΔP / ρ)",
                "Nicotra" to "q = k * √ΔP",
                "Common probe (e.g. FloXact)" to "q = k * √ΔP"
            )

            db.getHistoryEntryById(historyId).use { calculateCursor ->
                if (calculateCursor.moveToFirst()) {
                    val tempDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("tempCelsius"))
                    tempCelsius.editText?.setText(tempDb.toString())

                    val humidityDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("relativeHumidity"))
                    relativeHumidity.editText?.setText(humidityDb.toString())

                    val atmPressureDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("atmPressure"))
                    atmPressure.editText?.setText(atmPressureDb.toString())

                    val statPressureDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("statPressure"))
                    statPressure.editText?.setText(statPressureDb.toString())

                    val calibrationFactorDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("calibrationFactor"))
                    calibrationFactor.editText?.setText(calibrationFactorDb.toString())

                    val pressureDropDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("pressureDrop"))
                    pressureDrop.editText?.setText(pressureDropDb.toString())

                    val companyDb = calculateCursor.getString(calculateCursor.getColumnIndexOrThrow("company"))
                    manufacturer.text = companyDb

                    val formulaText = formuls[companyDb]
                    formula.text = formulaText


                    val finalDensityValueDb = calculateCursor.getDoubleOrNull(calculateCursor.getColumnIndexOrThrow("finalDensityValue"))
                    val finalConsumptionValueDb = calculateCursor.getDoubleOrNull(calculateCursor.getColumnIndexOrThrow("finalConsumptionValue"))

                    result.text = String.format(
                        " Плотность: %.4f кг/м³\n\n Расход: %.2f м³/${if (companyDb == "FläktWoods") "c" else "ч"}",
                        finalDensityValueDb,
                        finalConsumptionValueDb
                    )
                }
            }
        }
    }


    fun saveAllFields() {
        if (historyId == -1L) {
            return
        }

        val temp = tempCelsius.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val relativeHumidity = relativeHumidity.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val atmPressure = atmPressure.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val statPressure = statPressure.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val calibrationFactor = calibrationFactor.editText?.text.toString().toDoubleOrNull() ?: 0.0
        val pressureDrop = pressureDrop.editText?.text.toString().toDoubleOrNull() ?: 0.0



        val calculateUpdates = mapOf(
            "tempCelsius" to temp,
            "relativeHumidity" to relativeHumidity,
            "atmPressure" to atmPressure,
            "statPressure" to statPressure,
            "calibrationFactor" to calibrationFactor,
            "pressureDrop" to pressureDrop,
            "finalDensityValue" to finalDensityValue,
            "finalConsumptionValue" to finalConsumptionValue
        )

        calculateUpdates.forEach { (fieldName, value) ->
            db.updateHistoryData(historyId, fieldName, value)
        }
    }
}