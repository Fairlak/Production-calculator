package com.example.calculator.activity

import OnParamClickListener
import ParamsAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.activity.HistoryCalculations
import com.example.calculator.R
import com.example.calculator.adapters.AdapterScroll

class MainMenu : AppCompatActivity(), OnParamClickListener {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var scrollView: ScrollView
    private lateinit var paramsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)


        drawerLayout = findViewById(R.id.drawer_layout)
        val mainLayout: ConstraintLayout = findViewById(R.id.main_layout)
        val showListButton: Button = findViewById(R.id.button)
        val decideButton: Button = findViewById(R.id.decide)
        val paramsButton: ImageButton = findViewById(R.id.paramsButton)
        val manufacturerTable: TextView = findViewById(R.id.manufacturer_table)
        val dataTable = findViewById<TableLayout>(R.id.data_table)
        val manufacturerFormula: TextView = findViewById(R.id.manufacturer_formula)
        scrollView = findViewById(R.id.scroll_view_container)
        val recyclerView: RecyclerView = findViewById(R.id.my_recycler_view)


        setupParamsRecyclerView()

        paramsButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val formuls = mapOf(
            "FläktWoods" to "q = (1/k) * √ΔP",
            "Rosenberg" to "q = k * √(2 * ΔP / ρ)",
            "Nicotra-Gebhardt" to "q = k * √(2 * ΔP / ρ)",
            "Comefri" to "q = k * √(2 * ΔP / ρ)",
            "Ziehl" to "q = k * √ΔP",
            "ebm-papst" to "q = k * √ΔP",
            "Gebhardt" to "q = k * √(2 * ΔP / ρ)",
            "Nicotra" to "q = k * √ΔP",
            "Common probe (e.g. FloXact)" to "q = k * √ΔP"
        )

        val manufacturers = listOf(
            "FläktWoods",
            "Rosenberg",
            "Nicotra-Gebhardt",
            "Comefri",
            "Ziehl",
            "ebm-papst",
            "Gebhardt",
            "Nicotra",
            "Common probe (e.g. FloXact)"
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AdapterScroll(manufacturers) { clickedItem ->
            showListButton.text = clickedItem
            manufacturerTable.text = clickedItem
            manufacturerFormula.text = formuls[clickedItem]
            scrollView.visibility = View.GONE
            dataTable.visibility = View.VISIBLE
            decideButton.visibility = View.VISIBLE
        }
        recyclerView.adapter = adapter

        showListButton.setOnClickListener {
            scrollView.visibility = if (scrollView.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        mainLayout.setOnClickListener {
            if (scrollView.visibility == View.VISIBLE) {
                scrollView.visibility = View.GONE
            }
        }

        decideButton.setOnClickListener {
            val manufacturerText = manufacturerTable.text.toString()
            val formulaText = manufacturerFormula.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("MANUFACTURER_KEY", manufacturerText)
            intent.putExtra("FORMULA_KEY", formulaText)
            startActivity(intent)
        }
    }


    private fun setupParamsRecyclerView() {
        paramsRecyclerView = findViewById(R.id.params_recyclerview)
        paramsRecyclerView.layoutManager = LinearLayoutManager(this)

        val data = listOf("Выбор компании", "Клиент", "История", "Настройки")
        val adapter = ParamsAdapter(data, this)
        paramsRecyclerView.adapter = adapter
    }


    override fun onParamClick(param: String) {
        when(param){
            "История" -> startActivity(Intent(this, HistoryCalculations::class.java))
        }
        Toast.makeText(this, "Выбран параметр: $param", Toast.LENGTH_SHORT).show()
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}