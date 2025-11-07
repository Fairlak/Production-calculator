package com.example.calculator.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.HistoryAdapter
import com.example.calculator.R


class HistoryCalculations : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_calculations)

        // 1. Находим RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.history_recycler_view)

        // 2. Создаем экземпляр DbHelper
        val db = DbHelper(this, null)

        // 3. Получаем данные (метод вернет ArrayList<String>)
        val historyData = db.getAllHistory()

        // 4. Создаем и устанавливаем наш новый адаптер
        val adapter = HistoryAdapter(historyData)
        recyclerView.adapter = adapter

        // 5. Устанавливаем LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}