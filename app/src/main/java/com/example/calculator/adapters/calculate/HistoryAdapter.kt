package com.example.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.storage.ReportData
import com.example.calculator.storage.calculate.DecisionResult
import java.util.ArrayList

class HistoryAdapter(private val historyList: ArrayList<DecisionResult>, private val onItemClicked: (DecisionResult) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultTextView: TextView = itemView.findViewById(R.id.text_view_result)
        val companyTextView: TextView = itemView.findViewById(R.id.text_view_company)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val entry = historyList[position]

        val resultText = "Плотность: %.4f,\nРасход: %.2f".format(entry.finalDensityValue, entry.finalConsumptionValue)
        holder.resultTextView.text = resultText

        holder.companyTextView.text = entry.company

        holder.dateTextView.text = entry.timeStamp

        holder.itemView.setOnClickListener {
            onItemClicked(entry)
        }
    }

    fun updateData(newHistoryList: ArrayList<DecisionResult>) {
        historyList.clear()
        historyList.addAll(newHistoryList)
        notifyDataSetChanged()
    }
}
