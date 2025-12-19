package com.example.calculator.activity.reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.reports.ReportMeasurementsAdapter
import com.example.calculator.storage.clients.MeasurementData

class ReportDataMeasurements : AppCompatActivity() {
    private val dbHelper = DbHelper(this, null)

    private var reportId: Long = -1L
    private var clientId: Long = -1L
    private lateinit var reportMeasurementsAdapter: ReportMeasurementsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_data_measurements)

        val reportMeasurementsRecyclerView: RecyclerView = findViewById(R.id.report_measurementsRecyclerView)
        val backClientButton: ImageButton = findViewById(R.id.back_to_report_clients_button)
        val importMeasurementButton: Button = findViewById(R.id.import_measurement_button)
        val cancelButton: Button = findViewById(R.id.cancel_report_measurement_button)
        val addReportMeasurementButton: Button = findViewById(R.id.add_report_measurement_button)
        val selectedClientName: TextView = findViewById(R.id.report_client_name_inMeasurement)



        reportId = intent.getLongExtra("REPORT_ID", -1L)
        clientId = intent.getLongExtra("CLIENT_ID", -1L)

        importMeasurementButton.isEnabled = false
        importMeasurementButton.alpha = 0.5f


        val measurementsData = if (clientId != -1L){
            dbHelper.getMeasurementData(clientId)
        } else {
            arrayListOf()
        }

        reportMeasurementsAdapter = ReportMeasurementsAdapter(measurementsData) { clickedEntry ->
            importMeasurementButton.isEnabled = true
            importMeasurementButton.alpha = 1.0f
        }


        reportMeasurementsRecyclerView.adapter = reportMeasurementsAdapter
        reportMeasurementsRecyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper.getClientDataEntryById(clientId).use { cursor ->
            if (cursor.moveToFirst()) {
                val nameDb = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                selectedClientName.text = nameDb
            }
        }

        addReportMeasurementButton.setOnClickListener {
            val measurement = MeasurementData()
            val newId = dbHelper.addMeasurement(measurement, clientId)

            if (newId != -1L) {

                val updatedMeasurements = dbHelper.getMeasurementData(clientId)
                reportMeasurementsAdapter.updateData(updatedMeasurements)

                val intent = Intent(this, ReportAddMeasurementActivity::class.java)
                intent.putExtra("ID", newId)
                startActivity(intent)

            }
        }


        backClientButton.setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, ReportDataActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
            finish()
        }

        importMeasurementButton.setOnClickListener {
            if (reportId != -1L){
                val selectedMeasurement = reportMeasurementsAdapter.getSelectedClient()
                if (selectedMeasurement != null) {
                    dbHelper.updateReport(reportId, "measurementId", selectedMeasurement.id)
                    dbHelper.updateReport(reportId, "clientId", clientId)


                    val intent = Intent(this, ReportDataActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        // Для Android 14+
                        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
                    } else {
                        // Для старых версий
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                    }

                    finish()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val clientsData = dbHelper.getMeasurementData(clientId)
        reportMeasurementsAdapter.updateData(clientsData)
    }


}