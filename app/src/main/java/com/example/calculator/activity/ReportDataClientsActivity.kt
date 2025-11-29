package com.example.calculator.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.ReportClientsAdapter
import com.example.calculator.storage.ClientData


class ReportDataClientsActivity : AppCompatActivity() {

    private var idDb: Long = -1L
    private lateinit var reportClientsAdapter: ReportClientsAdapter
    private val db by lazy { DbHelper(this, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_data_clients)

        val reportClientsRecyclerView: RecyclerView = findViewById(R.id.report_clientsRecyclerView)
        val backReportButton: ImageButton = findViewById(R.id.back_to_report_button)
        val importClientButton: Button = findViewById(R.id.import_client_button)
        val cancelButton: Button = findViewById(R.id.cancel_report_client_button)
        val reportClientsSearchView = findViewById<SearchView>(R.id.report_clients_searchView)
        val clientsData = db.getClientData()


        idDb = intent.getLongExtra("ID", -1L)
        importClientButton.isEnabled = false
        importClientButton.alpha = 0.5f



        reportClientsAdapter = ReportClientsAdapter(
            clientList = clientsData,
            context = this,

            onItemClicked = { clientData ->
                Toast.makeText(this, "Выбран клиент: ${clientData.name}", Toast.LENGTH_SHORT).show()
                importClientButton.isEnabled = true
                importClientButton.alpha = 1.0f
            },
            onMeasurementsClicked = { clientData ->
                val intent = Intent(this, ReportDataMeasurements::class.java)
                intent.putExtra("REPORT_ID", idDb)
                intent.putExtra("CLIENT_ID", clientData.id)
                startActivity(intent)
            }
        )



        reportClientsRecyclerView.adapter = reportClientsAdapter
        reportClientsRecyclerView.layoutManager = LinearLayoutManager(this)

        backReportButton.setOnClickListener {
            finish()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }
        cancelButton.setOnClickListener {
            finish()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }

        importClientButton.setOnClickListener {
            if (idDb != -1L){
                val selectedClient = reportClientsAdapter.getSelectedClient()
                if (selectedClient != null){
                    db.updateReport(idDb, "clientId", selectedClient.id)
                    db.updateReport(idDb, "measurementId", -1L)
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
            finish()
        }

        reportClientsSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                reportClientsSearchView.clearFocus()
                if (query != null) {
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    reportClientsAdapter.filterList(newText)
                }
                return true
            }
        })



    }
}