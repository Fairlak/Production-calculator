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
        val reportClientsSearchView = findViewById<SearchView>(R.id.report_clients_searchView)
        val clientsData = db.getClientData()


        idDb = intent.getLongExtra("ID", -1L)




        reportClientsAdapter = ReportClientsAdapter(
            clientList = clientsData,
            context = this,

            onItemClicked = { clientData ->
                Toast.makeText(this, "Выбран клиент: ${clientData.name}", Toast.LENGTH_SHORT).show()
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
        }

        importClientButton.setOnClickListener {
            if (idDb != -1L){
                val selectedClient = reportClientsAdapter.getSelectedClient()
                if (selectedClient != null){
                    db.updateReport(idDb, "clientId", selectedClient.id)
                    db.updateReport(idDb, "measurementId", -1L)
                }
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