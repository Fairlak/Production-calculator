package com.example.calculator.adapters.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.storage.clients.ClientData
import java.util.ArrayList

class ReportClientsAdapter(
    private var clientList: ArrayList<ClientData>,
    private val onItemClicked: (ClientData) -> Unit,
    private val onMeasurementsClicked: (ClientData) -> Unit,
    context: Context
) : RecyclerView.Adapter<ReportClientsAdapter.ReportClientsViewHolder>() {

    private var fullClientList = ArrayList(clientList)
    private val dbHelper: DbHelper = DbHelper(context, null)
    private var selectedPosition = -1

    class ReportClientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reportClientName: TextView = itemView.findViewById(R.id.report_client_name_inReport)
        val reportClientObjectsMeterage: TextView = itemView.findViewById(R.id.report_objects_meterage)
        val radioButton: RadioButton = itemView.findViewById(R.id.report_client_radioButton)
        val toSelectMeasurementsButton: ImageButton = itemView.findViewById(R.id.to_select_measurements_button)
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
            onItemClicked(clientList[currentPosition])

        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
            onItemClicked(clientList[currentPosition])
        }

        holder.toSelectMeasurementsButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                updateSelection(currentPosition)
            }
            onMeasurementsClicked(clientList[currentPosition])
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

    fun updateData(newClientList: ArrayList<ClientData>) {
        clientList.clear()
        clientList.addAll(newClientList)
        fullClientList = newClientList
        notifyDataSetChanged()
    }

    fun filterList(query: String) {
        val filteredList = ArrayList<ClientData>()

        if (query.isEmpty()) {
            filteredList.addAll(fullClientList)
        } else {
            val filterPattern = query.lowercase().trim()
            for (item in fullClientList) {
                if (item.name.lowercase().contains(filterPattern)) {
                    filteredList.add(item)
                }
            }
        }
        clientList = filteredList
        notifyDataSetChanged()
    }



}