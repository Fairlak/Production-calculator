package com.example.calculator.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.storage.ClientData
import java.util.ArrayList

class ReportClientsAdapter(
    private var clientList: ArrayList<ClientData>,
    private val onItemClicked: (ClientData) -> Unit,
    context: Context
) : RecyclerView.Adapter<ReportClientsAdapter.ReportClientsViewHolder>() {


    private val dbHelper: DbHelper = DbHelper(context, null)
    private var selectedPosition = -1

    class ReportClientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reportClientName: TextView = itemView.findViewById(R.id.report_client_name_inReport)
        val reportClientObjectsMeterage: TextView = itemView.findViewById(R.id.report_objects_meterage)
        val radioButton: RadioButton = itemView.findViewById(R.id.report_client_radioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportClientsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_clients_item, parent, false)
        return ReportClientsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportClientsViewHolder, position: Int) {
        val client = clientList[position]

        val clientObjects = dbHelper.getMeasurementData(client.id)


        holder.reportClientName.text = if (client.name != "") client.name else "Имя клиента"
        holder.radioButton.isChecked = (position == selectedPosition)
        holder.reportClientObjectsMeterage.text = "Обьекты измерения: ${clientObjects.size}"



        holder.radioButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
            onItemClicked(clientList[currentPosition])
        }
    }

    override fun getItemCount(): Int = clientList.size


    private fun updateSelection(position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        val previousPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    fun getSelectedClient(): ClientData? {
        if (selectedPosition != -1) {
            return clientList[selectedPosition]
        }
        return null
    }



}