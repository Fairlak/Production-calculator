package com.example.calculator.activity

import SortingAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_calculations)
        val historyRecyclerView: RecyclerView = findViewById(R.id.history_recycler_view)
        val db = DbHelper(this, null)
        val historyData = db.getAllHistory()
        val historyAdapter = HistoryAdapter(historyData){ clickedEntry ->
            val selectedId = clickedEntry.id
            val toastText = "Вы нажали на запись от: ${clickedEntry.company}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent.putExtra("ID", selectedId))
        }
        historyData.sortByDescending { it.timeStamp }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)


        val sortingButton: Button = findViewById(R.id.button_sort)
        val sortingRecyclerView: RecyclerView = findViewById(R.id.sorting)

        val options = listOf(
            SortingOption("По дате", "↑", SortType.DATE_ASC),
            SortingOption("По дате", "↓", SortType.DATE_DESC),
            SortingOption("По имени", "↑", SortType.NAME_ASC),
            SortingOption("По имени", "↓", SortType.NAME_DESC)

        )


        sortingButton.setOnClickListener {
            sortingRecyclerView.visibility = if (sortingRecyclerView.isGone) View.VISIBLE else View.GONE
        }



        val sortingAdapter = SortingAdapter(options) { sortType ->
            when (sortType) {
                SortType.DATE_ASC -> {
                    sortingRecyclerView.visibility = View.GONE
                    Toast.makeText(this, "Сортировка по дате (возр.)", Toast.LENGTH_SHORT).show()
                    sortingButton.text = "Сортировка по дате (возр.)"
                    historyData.sortBy { it.timeStamp }
                }
                SortType.DATE_DESC -> {
                    sortingRecyclerView.visibility = View.GONE
                    Toast.makeText(this, "Сортировка по дате (убыв.)", Toast.LENGTH_SHORT).show()
                    sortingButton.text = "Сортировка по дате (убыв.)"

                    historyData.sortByDescending { it.timeStamp }
                }
                SortType.NAME_ASC -> {
                    sortingRecyclerView.visibility = View.GONE
                    Toast.makeText(this, "Сортировка по имени (возр.)", Toast.LENGTH_SHORT).show()
                    sortingButton.text = "Сортировка по имени (возр.)"

                    historyData.sortBy { it.company.lowercase() }
                }
                SortType.NAME_DESC -> {
                    sortingRecyclerView.visibility = View.GONE
                    Toast.makeText(this, "Сортировка по имени (убыв.)", Toast.LENGTH_SHORT).show()
                    sortingButton.text = "Сортировка по имени (убыв.)"

                    historyData.sortByDescending { it.company.lowercase() }
                }

            }
            historyAdapter.notifyDataSetChanged()

        }

        sortingRecyclerView.layoutManager = LinearLayoutManager(this)
        sortingRecyclerView.adapter = sortingAdapter

    }
}