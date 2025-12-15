package com.example.calculator.adapters.reports

import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.storage.clients.MeasurementData
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.calculator.R


class ReportMeasurementsAdapter(
    private var reportMeasurementsList: ArrayList<MeasurementData>,
    private val onItemClicked: (MeasurementData) -> Unit,
) : RecyclerView.Adapter<ReportMeasurementsAdapter.ReportMeasurementViewHolder>() {

    private var fullReportMeasurementsList = ArrayList(reportMeasurementsList)
    private var selectedPosition = -1

    class ReportMeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reportMeterageName: TextView = itemView.findViewById(R.id.report_point_name)
        val radioButton: RadioButton = itemView.findViewById(R.id.report_point_radioButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_measurements_item, parent, false)
        return ReportMeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportMeasurementViewHolder, position: Int) {
        val measurement = reportMeasurementsList[position]

        holder.radioButton.isChecked = (position == selectedPosition)
        holder.reportMeterageName.text = if (measurement.pointName != "") measurement.pointName else "Название места измерения"


        holder.radioButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
            onItemClicked(reportMeasurementsList[currentPosition])
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
            onItemClicked(reportMeasurementsList[currentPosition])
        }
    }

    override fun getItemCount(): Int = fullReportMeasurementsList.size



    private fun updateSelection(position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        val previousPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    fun getSelectedClient(): MeasurementData? {
        if (selectedPosition != -1) {
            return reportMeasurementsList[selectedPosition]
        }
        return null
    }

    fun updateData(newMeasurementList: java.util.ArrayList<MeasurementData>) {
        reportMeasurementsList.clear()
        reportMeasurementsList.addAll(newMeasurementList)
        fullReportMeasurementsList = newMeasurementList
        notifyDataSetChanged()
    }



}
