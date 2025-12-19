package com.example.calculator.activity.reports

import SortingAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.HistoryAdapter
import com.example.calculator.R
import com.example.calculator.storage.SortType
import com.example.calculator.storage.SortingOption

class ReportCalculationsActivity : AppCompatActivity() {
    private var idDb: Long = -1L
    private val db by lazy { DbHelper(this, null) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_calculations)

        idDb = intent.getLongExtra("ID", -1L)

        val historyData = db.getAllHistory()
        val reporCalcRecyclerView: RecyclerView = findViewById(R.id.repor_calc_recycler_view)
        val cancelReportCalcButton:ImageButton = findViewById(R.id.cancel_report_calc_button)

        val historyAdapter = HistoryAdapter(historyData) { clickedEntry ->
            val selectedId = clickedEntry.id
            db.updateReport(idDb, "historyId", selectedId)
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

        historyData.sortByDescending { it.timeStamp }
        reporCalcRecyclerView.adapter = historyAdapter
        reporCalcRecyclerView.layoutManager = LinearLayoutManager(this)



        val sortingButton: Button = findViewById(R.id.button_repor_calc_sort)
        val sortingRecyclerView: RecyclerView = findViewById(R.id.repor_calc_sorting)


        val options = listOf(
            SortingOption("По дате", "↑", SortType.DATE_ASC),
            SortingOption("По дате", "↓", SortType.DATE_DESC),
            SortingOption("По имени", "↑", SortType.NAME_ASC),
            SortingOption("По имени", "↓", SortType.NAME_DESC)

        )

        sortingButton.setOnClickListener {
            sortingRecyclerView.visibility = if (sortingRecyclerView.isGone) View.VISIBLE else View.GONE
        }

        cancelReportCalcButton.setOnClickListener {
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


        val sortingAdapter = SortingAdapter(options) { sortType ->
            when (sortType) {
                SortType.DATE_ASC -> {
                    sortingRecyclerView.visibility = View.GONE
                    sortingButton.text = "Сортировка по дате (возр.)"
                    historyData.sortBy { it.timeStamp }
                }
                SortType.DATE_DESC -> {
                    sortingRecyclerView.visibility = View.GONE
                    sortingButton.text = "Сортировка по дате (убыв.)"

                    historyData.sortByDescending { it.timeStamp }
                }
                SortType.NAME_ASC -> {
                    sortingRecyclerView.visibility = View.GONE
                    sortingButton.text = "Сортировка по имени (возр.)"

                    historyData.sortBy { it.company.lowercase() }
                }
                SortType.NAME_DESC -> {
                    sortingRecyclerView.visibility = View.GONE
                    sortingButton.text = "Сортировка по имени (убыв.)"

                    historyData.sortByDescending { it.company.lowercase() }
                }

            }
            historyAdapter.notifyDataSetChanged()

        }

        sortingRecyclerView.layoutManager = LinearLayoutManager(this)
        sortingRecyclerView.adapter = sortingAdapter
    }

    override fun onBackPressed() {
        finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Для Android 14+
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            // Для старых версий
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
        super.onBackPressed()
    }
}