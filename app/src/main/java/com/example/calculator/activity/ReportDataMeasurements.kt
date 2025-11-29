package com.example.calculator.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.ReportClientsAdapter
import com.example.calculator.adapters.ReportMeasurementsAdapter

class ReportDataMeasurements : AppCompatActivity() {
    private val dbHelper by lazy { DbHelper(this, null) }

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


        reportId = intent.getLongExtra("REPORT_ID", -1L)
        clientId = intent.getLongExtra("CLIENT_ID", -1L)


        val measurementsData = if (clientId != -1L){
            dbHelper.getMeasurementData(clientId)
        } else {
            arrayListOf()
        }

        reportMeasurementsAdapter = ReportMeasurementsAdapter(measurementsData) { clickedEntry ->
            val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
        }

        reportMeasurementsRecyclerView.adapter = reportMeasurementsAdapter
        reportMeasurementsRecyclerView.layoutManager = LinearLayoutManager(this)


        backClientButton.setOnClickListener {
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

                    finish()
                }
            }
        }



    }


}