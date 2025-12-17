package com.example.calculator.activity.clients

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MeasurementsDataActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_measurements_data)

        val comeBack: ImageButton = findViewById(R.id.back_to_client_data_button)



        val overlayViewDeleteClient: View = findViewById(R.id.overlay_view_delete_measurement)
        val openWarningDeleteClientButton: ImageButton = findViewById(R.id.open_warning_delete_measurement_button)
        val deleteCancelMeasurementButton: Button = findViewById(R.id.delete_cancel_measurement_button)
        val deleteButton: Button = findViewById(R.id.delete_measurement_button)
        val warningDeleteMeasurementLayout: View = findViewById(R.id.warning_delete_measurement_layout)


        idDb = intent.getLongExtra("ID", -1L)


        pointName = findViewById(R.id.point_name)
        installationNumber = findViewById(R.id.installation_number)
        installationName = findViewById(R.id.installation_name)
        manufacture = findViewById(R.id.manufacture)
        yearRelease = findViewById(R.id.year_release)
        serialNumber = findViewById(R.id.serial_number)
        note = findViewById(R.id.note)
        pointNameStatic = findViewById(R.id.measurement_location_static)



        comeBack.setOnClickListener {
            saveAllFields()
            finish()
        }

        deleteButton.setOnClickListener {
            if (idDb != -1L) {
                dbHelper.deleteMeasurement(idDb)
            }
            finish()
        }

        openWarningDeleteClientButton.setOnClickListener {
            overlayViewDeleteClient.visibility = View.VISIBLE
            warningDeleteMeasurementLayout.visibility = View.VISIBLE
        }

        deleteCancelMeasurementButton.setOnClickListener {
            overlayViewDeleteClient.visibility = View.GONE
            warningDeleteMeasurementLayout.visibility = View.GONE
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAllFields()
                finish()
            }
        })

        if (idDb != -1L) {
            val dbHelper = DbHelper(this, null)
            dbHelper.getMeasurementEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val pointNameDb = cursor.getString(cursor.getColumnIndexOrThrow("pointName"))
                    pointName.editText?.setText(pointNameDb)
                    pointNameStatic.text = if (pointNameDb == "") "Название места измерения" else pointNameDb

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