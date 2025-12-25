package com.example.calculator.adapters.clients

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.storage.clients.ClientData
import java.util.ArrayList


class ClientsAdapter(private var clientList: ArrayList<ClientData>, private val onItemClicked: (ClientData) -> Unit, private val context: Context) :
    RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {
    private val dbHelper: DbHelper = DbHelper(context, null)
    private var fullClientList = ArrayList(clientList)

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val clientObjectsMeterage: TextView = itemView.findViewById(R.id.objects_meterage)
        val clientInitialsCircle: TextView = itemView.findViewById(R.id.client_initials_circle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.clients_item, parent, false)
        return ClientViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clientList[position]
        val clientObjects = dbHelper.getMeasurementData(client.id)

        holder.clientName.text = if (client.name != "") client.name else "Имя клиента"
        holder.clientObjectsMeterage.text = "Обьекты измерения: ${clientObjects.size}"
        holder.clientInitialsCircle.text = if (client.name.isNotBlank()) {
            getInitials(client.name)
        } else {
            getInitials("Имя клиента")
        }

        holder.itemView.setOnClickListener {
            onItemClicked(client)
        }

    }

    private fun getInitials(name: String): String {
        return name.split(" ").filter { it.isNotEmpty() }.joinToString("") { it.first().uppercase() }.take(4)
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
