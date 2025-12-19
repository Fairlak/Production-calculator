package com.example.calculator.activity.calculate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.calculator.DbHelper
import com.example.calculator.storage.calculate.DecisionResult
import com.example.calculator.FormulaCalc
import com.example.calculator.storage.calculate.InputData
import com.example.calculator.R
import com.example.calculator.activity.clients.MeasurementsDataActivity
import com.example.calculator.activity.reports.ReportsActivity
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var historyId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)

        val mainCalcButton: Button = findViewById(R.id.main_calculate_button)
        val mainCreateReportButton: Button = findViewById(R.id.main_create_report)
        val mainBackButton: ImageButton = findViewById(R.id.main_calc_back_button)

        val itog = findViewById<TextView>(R.id.itog)

        var manufacturer = intent.getStringExtra("MANUFACTURER_KEY")
        val formula = intent.getStringExtra("FORMULA_KEY")


        val manufacturerTextView: TextView = findViewById(R.id.copied_manufacturer)
        val formulaTextView: TextView = findViewById(R.id.copied_formula)



        val mainView: View = findViewById(R.id.main_calc)
        val toolBar: View = findViewById(R.id.calc_tool_bar)


        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            toolBar.updatePadding(top = systemBars.top)
            val bottomPadding = ime.bottom.coerceAtLeast(systemBars.bottom)
            view.updatePadding(bottom = bottomPadding)
            windowInsets
        }

        manufacturerTextView.text = manufacturer
        formulaTextView.text = formula


        mainCreateReportButton.isEnabled = false
        mainCreateReportButton.alpha = 0.5f



        mainCalcButton.setOnClickListener {
            val inputData = createStorageInstance()
            val processor = FormulaCalc(inputData)
            val finalDensityValue = processor.airDensityCalculation()
            var finalConsumptionValue = processor.consumption(manufacturer)
            if (finalConsumptionValue.isNaN()) finalConsumptionValue = 0.0


            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val decisionResult = DecisionResult(
                finalDensityValue = finalDensityValue,
                finalConsumptionValue = finalConsumptionValue,
                company = manufacturer!!,
                timeStamp = currentDate
            )
            val db = DbHelper(this, null)
            historyId = db.addHistory(inputData, decisionResult)

            mainCreateReportButton.isEnabled = true
            mainCreateReportButton.alpha = 1.0f



            itog.text = String.format(
                " Плотность: %.4f кг/м³\n\n Расход: %.2f м³/${if (manufacturer == "FläktWoods") "c" else "ч"}",
                finalDensityValue,
                finalConsumptionValue
            )

        }

        mainBackButton.setOnClickListener {
            finish()
        }

        mainCreateReportButton.setOnClickListener {
            if (historyId != -1L){
                val intent = Intent(this, ReportsActivity::class.java)
                intent.putExtra("historyId", historyId)
                startActivity(intent)
            }
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