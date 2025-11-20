package com.example.calculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import com.example.calculator.storage.MeasurementData
import java.util.ArrayList

class MeasurementsAdapter(private var measurementsList: ArrayList<MeasurementData>, private val onItemClicked: (MeasurementData) -> Unit) :
    RecyclerView.Adapter<MeasurementsAdapter.MeasurementViewHolder>() {
    private var fullMeasurementsList = ArrayList(measurementsList)

    class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val meterageTextView: TextView = itemView.findViewById(R.id.meterage_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.measuring_points_item, parent, false)
        return MeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurement = measurementsList[position]


        holder.meterageTextView.text = if (measurement.pointName != "") measurement.pointName else "Название места измерения"

        holder.itemView.setOnClickListener {
            onItemClicked(measurement)
        }

    }

    override fun getItemCount(): Int = measurementsList.size


    fun updateData(newMeasurementList: ArrayList<MeasurementData>) {
        measurementsList.clear()
        measurementsList.addAll(newMeasurementList)
        fullMeasurementsList = newMeasurementList
        notifyDataSetChanged()
    }





}