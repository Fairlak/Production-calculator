package com.example.calculator.activity


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.activity.ClientDataActivity
import com.example.calculator.adapters.ClientsAdapter
import com.example.calculator.storage.ClientData

class ClientsActivity : AppCompatActivity() {

    private lateinit var clientAdapter: ClientsAdapter
    private val db by lazy { DbHelper(this, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)

        val clientsRecyclerView: RecyclerView = findViewById(R.id.clientsRecyclerView)
        val clientsData = db.getClientData()
        clientAdapter = ClientsAdapter(clientsData){ clickedEntry ->
            val selectedId = clickedEntry.id
            val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ClientDataActivity::class.java)
            startActivity(intent.putExtra("ID", selectedId))
        }

        clientsRecyclerView.adapter = clientAdapter
        clientsRecyclerView.layoutManager = LinearLayoutManager(this)

        val createClientButton: Button = findViewById(R.id.add_client_button)

        createClientButton.setOnClickListener {
            val client = ClientData()
            db.addClient(client)

            val updatedClients = db.getClientData()
            clientAdapter.updateData(updatedClients)


        }
    }
}
