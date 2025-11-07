package com.example.calculator.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.button1)
        val itog = findViewById<TextView>(R.id.itog)

        val manufacturer = intent.getStringExtra("MANUFACTURER_KEY")
        val formula = intent.getStringExtra("FORMULA_KEY")
        val manufacturerTextView: TextView = findViewById(R.id.copied_manufacturer)
        val formulaTextView: TextView = findViewById(R.id.copied_formula)


        manufacturerTextView.text = manufacturer
        formulaTextView.text = formula


        button.setOnClickListener {
            val inputData = createStorageInstance()
            val processor = FormulaCalc(inputData)
            val finalDensityValue = processor.airDensityCalculation()
            val finalConsumptionValue = processor.consumption(manufacturer)

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val decisionResult = DecisionResult(finalDensityValue, finalConsumptionValue, manufacturer!!, currentDate)
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
        val calibration_factor = findViewById<TextInputLayout>(R.id.calibration_factor)
        val pressure_drop = findViewById<TextInputLayout>(R.id.pressure_drop)



        val tempStr = tempCelsius.editText?.text.toString()
        val humStr = relativeHumidity.editText?.text.toString()
        val atmStr = atmPressure.editText?.text.toString()
        val statStr = statPressure.editText?.text.toString()
        val calibStr = calibration_factor.editText?.text.toString()
        val dropStr = pressure_drop.editText?.text.toString()



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