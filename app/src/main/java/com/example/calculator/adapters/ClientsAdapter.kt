package com.example.calculator.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import com.example.calculator.storage.ClientData
import java.util.ArrayList


class ClientsAdapter(private var clientList: ArrayList<ClientData>, private val onItemClicked: (ClientData) -> Unit) :
    RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {
    private var fullClientList = ArrayList(clientList)

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val clientNameTextView: TextView = itemView.findViewById(R.id.client_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.clients_item, parent, false)
        return ClientViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clientList[position]


        holder.clientNameTextView.text = if (client.name != "") client.name else "Имя клиента"

        holder.itemView.setOnClickListener {
            onItemClicked(client)
        }

    }

    override fun getItemCount(): Int = clientList.size



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
