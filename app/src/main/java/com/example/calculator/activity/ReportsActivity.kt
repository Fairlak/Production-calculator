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
import com.example.calculator.adapters.ReportsAdapter
import com.example.calculator.storage.ReportData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var reportAdapter: ReportsAdapter
    private val db by lazy { DbHelper(this, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reports)

        val addReportButton: Button = findViewById(R.id.add_report_button)
        val reportsRecyclerView: RecyclerView = findViewById(R.id.reportsRecyclerView)
        val backMenuButton: ImageButton = findViewById(R.id.back_to_menu_button)
        val searchReport = findViewById<SearchView>(R.id.searchReport)




        val reportData = db.getReportsData()
        reportAdapter = ReportsAdapter(reportData, { clickedEntry ->
            val selectedId = clickedEntry.id
            val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ReportDataActivity::class.java)
            intent.putExtra("ID", selectedId)
            startActivity(intent)

        }, this)

        reportsRecyclerView.adapter = reportAdapter
        reportsRecyclerView.layoutManager = LinearLayoutManager(this)


        addReportButton.setOnClickListener {

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val report = ReportData(
                reportTime = currentDate
            )
            val newId = db.addReport(report)

            if (newId != -1L) {
                val updatedReports = db.getReportsData()
                reportAdapter.updateData(updatedReports)

            }

            val toastText = "${reportData.size}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
        }

        backMenuButton.setOnClickListener {
            finish()
        }


        searchReport.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchReport.clearFocus()
                if (query != null) {
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    reportAdapter.filterList(newText)
                }
                return true
            }
        })

    }
    override fun onResume() {
        super.onResume()
        val measurementsData = db.getReportsData()
        reportAdapter.updateData(measurementsData)
    }
}