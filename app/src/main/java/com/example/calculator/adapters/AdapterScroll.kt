package com.example.calculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R


class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.item_text)
}


class AdapterScroll(
    private val data: List<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemText = data[position]

        holder.textView.text = itemText

        holder.itemView.setOnClickListener {
            onItemClicked(itemText)
        }
    }
}
