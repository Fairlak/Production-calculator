package com.example.calculator.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.calculator.DbHelper
import com.example.calculator.R

class ReportDataActivity : AppCompatActivity() {

    private var idDb: Long = -1L
    private val db by lazy { DbHelper(this, null) }

    private lateinit var createReportDate: TextView
    private lateinit var clientSelectedNameStatic: TextView
    private lateinit var deleteClientButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_data)

        createReportDate = findViewById(R.id.create_report_date)
        clientSelectedNameStatic = findViewById(R.id.selected_client_name)

        val backReportsButton: ImageButton = findViewById(R.id.back_to_reports_button)
        val deleteReportButton: ImageButton = findViewById(R.id.delete_report_button)
        val mainClientLayout: ConstraintLayout = findViewById(R.id.main_client)
        deleteClientButton = findViewById(R.id.delete_client_report_button)

        idDb = intent.getLongExtra("ID", -1L)



        backReportsButton.setOnClickListener {
            finish()
        }

        deleteReportButton.setOnClickListener {
            if (idDb != -1L) {
                db.deleteReport(idDb)
            }
            finish()
        }

        mainClientLayout.setOnClickListener {
            val intent = Intent(this, ReportDataClientsActivity::class.java)
            intent.putExtra("ID", idDb)
            startActivity(intent)
        }

        deleteClientButton.setOnClickListener {
            if (idDb != -1L) {
                db.updateReport(idDb, "clientId", "")
                db.updateReport(idDb, "measurementId", "")
                refreshClientData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshClientData()
    }

    private fun refreshClientData() {
        if (idDb != -1L) {
            db.getReportDataEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val timeDb = cursor.getString(cursor.getColumnIndexOrThrow("reportTime"))
                    createReportDate.text = timeDb

                    val clientIdDb = cursor.getLong(cursor.getColumnIndexOrThrow("clientId"))
                    val measurementIdDb = cursor.getLong(cursor.getColumnIndexOrThrow("measurementId"))

                    var clientNameString = ""
                    var measurementPointString = ""

                    db.getClientDataEntryById(clientIdDb).use { clientCursor ->
                        if (clientCursor.moveToFirst()){
                            val name = clientCursor.getString(clientCursor.getColumnIndexOrThrow("name"))
                            clientNameString = if (!name.isNullOrEmpty()) name else "Имя клиента"
                        }
                    }


                    db.getMeasurementEntryById(measurementIdDb).use { measurementCursor ->
                        if (measurementCursor.moveToFirst()) {
                            val pointName = measurementCursor.getString(measurementCursor.getColumnIndexOrThrow("pointName"))
                            measurementPointString = if (!pointName.isNullOrEmpty()) pointName else ""
                        }
                    }

                    if (clientNameString.isNotEmpty()) {
                        clientSelectedNameStatic.visibility = View.VISIBLE
                        deleteClientButton.visibility = View.VISIBLE

                        val fullText = if (measurementPointString.isNotEmpty()) {
                            "$clientNameString $measurementPointString"
                        } else {
                            clientNameString
                        }
                        clientSelectedNameStatic.text = fullText
                    } else {
                        clientSelectedNameStatic.visibility = View.GONE
                        deleteClientButton.visibility = View.GONE
                        clientSelectedNameStatic.text = "Клиент не выбран"
                    }

                }
            }
        }
    }
}

