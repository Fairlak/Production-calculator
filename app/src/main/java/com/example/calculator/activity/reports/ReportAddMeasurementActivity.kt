package com.example.calculator.activity.reports

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReportAddMeasurementActivity : AppCompatActivity() {
    private var idDb: Long = -1L
    private val dbHelper by lazy { DbHelper(this, null) }


    private lateinit var pointName: TextInputLayout
    private lateinit var installationNumber: TextInputLayout
    private lateinit var installationName: TextInputLayout
    private lateinit var manufacture: TextInputLayout
    private lateinit var yearRelease: TextInputLayout
    private lateinit var serialNumber: TextInputLayout
    private lateinit var note: TextInputLayout
    private lateinit var pointNameStatic: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_add_measurement)

        val comeBack: ImageButton = findViewById(R.id.back_to_report_clients_button)

        pointName = findViewById(R.id.add_point_name)
        installationNumber = findViewById(R.id.add_installation_number)
        installationName = findViewById(R.id.add_installation_name)
        manufacture = findViewById(R.id.add_manufacture)
        yearRelease = findViewById(R.id.add_year_release)
        serialNumber = findViewById(R.id.add_serial_number)
        note = findViewById(R.id.add_note)
        pointNameStatic = findViewById(R.id.add_measurement_location_static)


        idDb = intent.getLongExtra("ID", -1L)


        comeBack.setOnClickListener {
            saveAllFields()
            finish()
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAllFields()
                finish()
            }
        })


        if (idDb != -1L) {
            dbHelper.getMeasurementEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val pointNameDb = cursor.getString(cursor.getColumnIndexOrThrow("pointName"))
                    pointName.editText?.setText(pointNameDb)
                    pointNameStatic.text = if (pointNameDb == "") "Имя клиента" else pointNameDb

                    val installationNumberDb = cursor.getString(cursor.getColumnIndexOrThrow("installationNumber"))
                    installationNumber.editText?.setText(installationNumberDb)

                    val installationNameDb = cursor.getString(cursor.getColumnIndexOrThrow("installationName"))
                    installationName.editText?.setText(installationNameDb)

                    val manufactureDb = cursor.getString(cursor.getColumnIndexOrThrow("manufacture"))
                    manufacture.editText?.setText(manufactureDb)

                    val yearReleaseDb = cursor.getString(cursor.getColumnIndexOrThrow("yearRelease"))
                    yearRelease.editText?.setText(yearReleaseDb)

                    val serialNumberDb = cursor.getString(cursor.getColumnIndexOrThrow("serialNumber"))
                    serialNumber.editText?.setText(serialNumberDb)

                    val noteDb = cursor.getString(cursor.getColumnIndexOrThrow("note"))
                    note.editText?.setText(noteDb)
                }
            }
        }

        val focusListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val editText = view as? TextInputEditText
                if (editText == null) return@OnFocusChangeListener
                val inputText = editText.text.toString()

                val nameField = when ((editText.parent.parent as? TextInputLayout)?.id) {
                    R.id.point_name -> "pointName"
                    R.id.installation_number -> "installationNumber"
                    R.id.installation_name -> "installationName"
                    R.id.manufacture -> "manufacture"
                    R.id.year_release -> "yearRelease"
                    R.id.serial_number -> "serialNumber"
                    R.id.note -> "note"
                    else -> null
                }
                if (nameField != null) {
                    val dbHelper = DbHelper(this, null)
                    dbHelper.updateMeasurement(idDb, nameField, inputText)
                }
                if (nameField == "pointName"){
                    pointNameStatic.text = inputText
                }

            }
        }

        pointName.editText?.onFocusChangeListener = focusListener
        installationNumber.editText?.onFocusChangeListener = focusListener
        installationName.editText?.onFocusChangeListener = focusListener
        manufacture.editText?.onFocusChangeListener = focusListener
        yearRelease.editText?.onFocusChangeListener = focusListener
        serialNumber.editText?.onFocusChangeListener = focusListener
        note.editText?.onFocusChangeListener = focusListener
    }

    fun saveAllFields() {
        if (idDb == -1L) {
            return
        }

        val pointName = pointName.editText?.text.toString()
        val installationNumber = installationNumber.editText?.text.toString()
        val installationName = installationName.editText?.text.toString()
        val manufacture = manufacture.editText?.text.toString()
        val yearRelease = yearRelease.editText?.text.toString()
        val serialNumber = serialNumber.editText?.text.toString()
        val note = note.editText?.text.toString()

        val measurementUpdates = mapOf(
            "pointName" to pointName,
            "installationNumber" to installationNumber,
            "installationName" to installationName,
            "manufacture" to manufacture,
            "yearRelease" to yearRelease,
            "serialNumber" to serialNumber,
            "note" to note
        )

        measurementUpdates.forEach { (fieldName, value) ->
            dbHelper.updateMeasurement(idDb, fieldName, value)
        }
    }
}