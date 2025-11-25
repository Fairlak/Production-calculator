package com.example.calculator.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.DbHelper
import com.example.calculator.storage.DecisionResult
import com.example.calculator.FormulaCalc
import com.example.calculator.storage.InputData
import com.example.calculator.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button1)
        val itog = findViewById<TextView>(R.id.itog)

        var manufacturer = intent.getStringExtra("MANUFACTURER_KEY")
        val formula = intent.getStringExtra("FORMULA_KEY")
        val idDB = intent.getLongExtra("ID", -1L)


        val manufacturerTextView: TextView = findViewById(R.id.copied_manufacturer)
        val formulaTextView: TextView = findViewById(R.id.copied_formula)
        val tempCelsius = findViewById<TextInputLayout>(R.id.temp)
        val relativeHumidity = findViewById<TextInputLayout>(R.id.wet)
        val atmPressure = findViewById<TextInputLayout>(R.id.atm_pressure)
        val statPressure = findViewById<TextInputLayout>(R.id.stat_pressure)
        val calibrationFactor = findViewById<TextInputLayout>(R.id.calibration_factor)
        val pressureDrop = findViewById<TextInputLayout>(R.id.pressure_drop)


        manufacturerTextView.text = manufacturer
        formulaTextView.text = formula



        if (idDB != -1L) {
            val dbHelper = DbHelper(this, null)
            dbHelper.getHistoryEntryById(idDB).use { cursor ->
                if (cursor.moveToFirst()) {

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



                    val companyDb = cursor.getString(cursor.getColumnIndexOrThrow("company"))
                    manufacturer = companyDb
                    manufacturerTextView.text = companyDb

                    val formula = formuls[companyDb]
                    formulaTextView.text = formula

                    val tempDb = cursor.getString(cursor.getColumnIndexOrThrow("tempCelsius"))
                    tempCelsius.editText?.setText(tempDb.toString())

                    val humidityDb = cursor.getString(cursor.getColumnIndexOrThrow("relativeHumidity"))
                    relativeHumidity.editText?.setText(humidityDb.toString())

                    val atmPressureDb = cursor.getString(cursor.getColumnIndexOrThrow("atmPressure"))
                    atmPressure.editText?.setText(atmPressureDb.toString())

                    val statPressureDb = cursor.getString(cursor.getColumnIndexOrThrow("statPressure"))
                    statPressure.editText?.setText(statPressureDb.toString())

                    val calibrationFactorDb = cursor.getString(cursor.getColumnIndexOrThrow("calibrationFactor"))
                    calibrationFactor.editText?.setText(calibrationFactorDb.toString())

                    val pressureDropDb = cursor.getString(cursor.getColumnIndexOrThrow("pressureDrop"))
                    pressureDrop.editText?.setText(pressureDropDb.toString())

                    val finalDensityValueDb = cursor.getString(cursor.getColumnIndexOrThrow("finalDensityValue")).toDoubleOrNull()
                    val finalConsumptionValueDb = cursor.getString(cursor.getColumnIndexOrThrow("finalConsumptionValue")).toDoubleOrNull()

                    itog.text = String.format(
                        "Плотность: %.4f кг/м³\n\nРасход: %.2f м³/${if (manufacturer == "FläktWoods") "c" else "ч"}",
                        finalDensityValueDb,
                        finalConsumptionValueDb
                    )
                }
            }
        }

        button.setOnClickListener {
            val inputData = createStorageInstance()
            val processor = FormulaCalc(inputData)
            val finalDensityValue = processor.airDensityCalculation()
            var finalConsumptionValue = processor.consumption(manufacturer)
            if (finalConsumptionValue.isNaN()) finalConsumptionValue = 0.0

            val toastText = "Расход ${finalConsumptionValue}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()




            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val decisionResult = DecisionResult(
                finalDensityValue = finalDensityValue,
                finalConsumptionValue = finalConsumptionValue,
                company = manufacturer!!,
                timeStamp = currentDate
            )
            val db = DbHelper(this, null)
            db.addHistory(inputData, decisionResult)
            


            itog.text = String.format(
                "Плотность: %.4f кг/м³\n\nРасход: %.2f м³/${if (manufacturer == "FläktWoods") "c" else "ч"}",
                finalDensityValue,
                finalConsumptionValue
            )

        }

    }

    fun createStorageInstance(): InputData {
        val tempCelsius = findViewById<TextInputLayout>(R.id.temp)
        val relativeHumidity = findViewById<TextInputLayout>(R.id.wet)
        val atmPressure = findViewById<TextInputLayout>(R.id.atm_pressure)
        val statPressure = findViewById<TextInputLayout>(R.id.stat_pressure)
        val calibrationFactor = findViewById<TextInputLayout>(R.id.calibration_factor)
        val pressureDrop = findViewById<TextInputLayout>(R.id.pressure_drop)



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
}