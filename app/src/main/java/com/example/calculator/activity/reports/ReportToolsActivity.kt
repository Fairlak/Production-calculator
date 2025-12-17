package com.example.calculator.activity.reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.reports.ReportToolsAdapter
import com.example.calculator.storage.ToolsData


class ReportToolsActivity : AppCompatActivity() {

    private var reportId: Long = -1L
    private lateinit var reportToolsAdapter: ReportToolsAdapter
    private val db by lazy { DbHelper(this, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_tools)


        val toolsRecyclerView: RecyclerView = findViewById(R.id.toolsRecyclerView)
        val addToolButton: Button = findViewById(R.id.add_tool_button)
        val cancelToolsButton: ImageButton = findViewById(R.id.tools_back_to_report_button)

        reportId = intent.getLongExtra("reportId", -1L)
        val toolsData = db.getToolsData(reportId)


        reportToolsAdapter = ReportToolsAdapter(toolsData,
            onItemClicked = { clickedEntry ->
                val selectedId = clickedEntry.id
                val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ReportToolDataActivity::class.java)
                startActivity(intent.putExtra("ID", selectedId))
            },
            onImageButtonClicked = {clickedEntry, position ->
                val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()

                val newId = db.addTool(reportId, clickedEntry)

                if (newId != -1L) {
                    val updatedTools = db.getToolsData(reportId)
                    reportToolsAdapter.updateData(updatedTools)

                }
            }
        )

        toolsRecyclerView.adapter = reportToolsAdapter
        toolsRecyclerView.layoutManager = LinearLayoutManager(this)


        addToolButton.setOnClickListener {
            val tool = ToolsData()
            val newId = db.addTool(reportId, tool)

            if (newId != -1L) {
                val updatedTools = db.getToolsData(reportId)
                reportToolsAdapter.updateData(updatedTools)

            }
        }

        cancelToolsButton.setOnClickListener {
            finish()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val toolsData = db.getToolsData(reportId)
        reportToolsAdapter.updateData(toolsData)
    }
}