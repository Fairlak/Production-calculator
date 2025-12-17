package com.example.calculator.adapters.reports


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import com.example.calculator.storage.ToolsData
import java.util.ArrayList

class ReportToolsAdapter(
    private var toolsList: ArrayList<ToolsData>,
    private val onItemClicked: (ToolsData) -> Unit,
    private val onImageButtonClicked: (ToolsData, Int) -> Unit
) : RecyclerView.Adapter<ReportToolsAdapter.ToolsViewHolder>() {

    class ToolsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val toolNameItem: TextView = itemView.findViewById(R.id.tool_name_item)
        val serialNumberItem: TextView = itemView.findViewById(R.id.serial_number_item)
        val toolCopyButton: ImageButton = itemView.findViewById(R.id.tool_copy_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tools_item, parent, false)
        return ToolsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ToolsViewHolder, position: Int) {
        val tools = toolsList[position]


        holder.toolNameItem.text = if (tools.toolName != "") tools.toolName else "Название прибора"
        holder.serialNumberItem.text = if (tools.serialNumber != "") tools.serialNumber else "Серийный номер"


        holder.itemView.setOnClickListener {
            onItemClicked(tools)
        }

        holder.toolCopyButton.setOnClickListener {
            onImageButtonClicked(tools, position)
        }
    }

    override fun getItemCount(): Int = toolsList.size


    fun updateData(newToolsList: ArrayList<ToolsData>) {
        toolsList.clear()
        toolsList.addAll(newToolsList)
        notifyDataSetChanged()
    }





}