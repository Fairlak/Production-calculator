package com.example.calculator.activity.clients


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.clients.ClientsAdapter
import com.example.calculator.storage.clients.ClientData

class ClientsActivity : AppCompatActivity() {

    private lateinit var clientAdapter: ClientsAdapter
    private val db by lazy { DbHelper(this, null) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)


        val searchView = findViewById<SearchView>(R.id.searchView)
        val clientsRecyclerView: RecyclerView = findViewById(R.id.clientsRecyclerView)
        val clientsData = db.getClientData()
        val backMenuButton: ImageButton = findViewById(R.id.clients_back_to_menu_button)

        clientAdapter = ClientsAdapter(clientsData, { clickedEntry ->
            val selectedId = clickedEntry.id
            val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ClientDataActivity::class.java)
            startActivity(intent.putExtra("ID", selectedId))
        }, this)

        clientsRecyclerView.adapter = clientAdapter
        clientsRecyclerView.layoutManager = LinearLayoutManager(this)

        val createClientButton: Button = findViewById(R.id.add_client_button)

        createClientButton.setOnClickListener {
            val client = ClientData()
            val newId = db.addClient(client)

            if (newId != -1L) {

                val updatedClients = db.getClientData()
                clientAdapter.updateData(updatedClients)

                val intent = Intent(this, ClientDataActivity::class.java)
                intent.putExtra("ID", newId)
                startActivity(intent)

            }
        }

        backMenuButton.setOnClickListener {
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                if (query != null) {
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    clientAdapter.filterList(newText)
                }
                return true
            }
        })


    }

    override fun onResume() {
        super.onResume()
        val clientsData = db.getClientData()
        clientAdapter.updateData(clientsData)
    }

}
