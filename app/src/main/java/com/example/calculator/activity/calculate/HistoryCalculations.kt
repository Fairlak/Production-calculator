package com.example.calculator.activity.calculate

import SortingAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.HistoryAdapter
import com.example.calculator.R
import com.example.calculator.storage.SortType
import com.example.calculator.storage.SortingOption
import androidx.core.view.isGone


class HistoryCalculations : AppCompatActivity() {

    private val db by lazy { DbHelper(this, null) }
    private lateinit var historyAdapter: HistoryAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_calculations)
        val historyRecyclerView: RecyclerView = findViewById(R.id.history_recycler_view)
        val historyData = db.getAllHistory()
        historyAdapter = HistoryAdapter(historyData){ clickedEntry ->
            val selectedId = clickedEntry.id
            val intent = Intent(this, UpdateCalculateActivity::class.java)
            intent.putExtra("HistoryId", selectedId)
            startActivity(intent)


        }
        historyData.sortByDescending { it.timeStamp }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)


        val sortingButton: Button = findViewById(R.id.calc_history_sort_button)
        val sortingRecyclerView: RecyclerView = findViewById(R.id.history_calc_sorting)
        val cancelHistoryCalcButton: ImageButton = findViewById(R.id.cancel_history_calc_button)


        val options = listOf(
            SortingOption("По дате", "↑", SortType.DATE_ASC),
            SortingOption("По дате", "↓", SortType.DATE_DESC),
            SortingOption("По имени", "↑", SortType.NAME_ASC),
            SortingOption("По имени", "↓", SortType.NAME_DESC)

        )

        sortingButton.setOnClickListener {
            sortingRecyclerView.visibility = if (sortingRecyclerView.isGone) View.VISIBLE else View.GONE
        }

        cancelHistoryCalcButton.setOnClickListener {
            sortingRecyclerView.visibility = View.GONE
            finish()
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

    override fun onResume() {
        super.onResume()
        val measurementsData = db.getAllHistory()
        historyAdapter.updateData(measurementsData)
    }
}