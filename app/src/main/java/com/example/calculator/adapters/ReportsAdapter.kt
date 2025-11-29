package com.example.calculator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.storage.ClientData
import com.example.calculator.storage.ReportData
import java.util.ArrayList
import kotlin.text.isNotEmpty
import kotlin.text.lowercase

class ReportsAdapter(
    private var reportList: ArrayList<ReportData>,
    private val onItemClicked: (ReportData) -> Unit,
    context: Context
) : RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder>() {

    private val dbHelper: DbHelper = DbHelper(context, null)
    private var fullReportList = ArrayList(reportList)


    class ReportsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reportClientName: TextView = itemView.findViewById(R.id.report_client_name)
        val reportCreateTime: TextView = itemView.findViewById(R.id.report_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reports_item, parent, false)
        return ReportsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        val report = reportList[position]

        holder.reportCreateTime.text = report.reportTime
        if (report.clientId != -1L){
            dbHelper.getClientDataEntryById(report.clientId).use { cursor ->
                if (cursor.moveToFirst()) {
                    val clientName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    if (!clientName.isNullOrEmpty()) {
                        val nameDb = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        holder.reportClientName.text = nameDb
                    } else holder.reportClientName.text = "Имя клиента"

                }
            }
        }else{
            holder.reportClientName.text = "Имя клиента"
        }

        holder.itemView.setOnClickListener {
            onItemClicked(report)
        }

    }

    override fun getItemCount(): Int = reportList.size

    fun updateData(newReportList: ArrayList<ReportData>) {
        reportList.clear()
        reportList.addAll(newReportList)
        notifyDataSetChanged()
    }

    fun filterList(query: String) {
        val filteredList = ArrayList<ReportData>()

        if (query.isEmpty()) {
            filteredList.addAll(fullReportList)
        } else {
            val filterPattern = query.lowercase().trim()
            for (item in fullReportList) {
                var clientName = ""

                if (item.clientId != -1L) {
                    dbHelper.getClientDataEntryById(item.clientId).use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex("name")
                            if (nameIndex != -1) {
                                clientName = cursor.getString(nameIndex)
                            }
                        }
                    }
                }
                if (clientName.isNotEmpty() && clientName.lowercase().contains(filterPattern)) {
                    filteredList.add(item)
                }

            }
        }
        reportList = filteredList
        notifyDataSetChanged()
    }



}
