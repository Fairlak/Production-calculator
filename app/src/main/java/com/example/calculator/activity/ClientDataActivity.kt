package com.example.calculator.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.ClientsAdapter
import com.example.calculator.adapters.MeasurementsAdapter
import com.example.calculator.storage.ClientData
import com.example.calculator.storage.MeasurementData
import com.google.android.material.textfield.TextInputLayout

class ClientDataActivity : AppCompatActivity() {
    private var idDb: Long = -1L
    private val dbHelper by lazy { DbHelper(this, null) }


    private lateinit var measurementsAdapter: MeasurementsAdapter


    private lateinit var clientName: TextInputLayout
    private lateinit var clientStreet: TextInputLayout
    private lateinit var clientCity: TextInputLayout
    private lateinit var clientCountry: TextInputLayout
    private lateinit var clientPhone: TextInputLayout
    private lateinit var clientEmail: TextInputLayout
    private lateinit var clientContact: TextInputLayout
    private lateinit var clientData: TextInputLayout
    private lateinit var clientNameStatic: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_client_data)

        val measurementsRecyclerView: RecyclerView = findViewById(R.id.measuring_points_RecyclerView)


        val comeBack: ImageButton = findViewById(R.id.back_to_clients_button)
        val deleteButton: ImageButton = findViewById(R.id.delete_client_button)
        val addPointsButton: Button = findViewById(R.id.add_measuring_points_button)
        val measuringPoints: TextView = findViewById(R.id.measuring_points)
        val contact: TextView = findViewById(R.id.contact)
        val clientLiner: LinearLayout = findViewById(R.id.client_liner)



        contact.alpha = 1.0f
        measuringPoints.alpha = 0.5f

        idDb = intent.getLongExtra("ID", -1L)

        clientNameStatic = findViewById(R.id.client_name_static)

        clientName = findViewById(R.id.client_name)
        clientStreet = findViewById(R.id.client_street)
        clientCity = findViewById(R.id.client_city)
        clientCountry = findViewById(R.id.client_country)
        clientPhone = findViewById(R.id.client_phone)
        clientEmail = findViewById(R.id.client_email)
        clientContact = findViewById(R.id.client_contact)
        clientData = findViewById(R.id.client_data)

        comeBack.setOnClickListener {
            saveAllFields()
            finish()
        }

        deleteButton.setOnClickListener {
            if (idDb != -1L) {
                dbHelper.deleteClient(idDb)
            }
            finish()
        }

        measuringPoints.setOnClickListener {
            contact.alpha = 0.5f
            measuringPoints.alpha = 1.0f

            addPointsButton.visibility = View.VISIBLE
            clientLiner.visibility = View.GONE
            measurementsRecyclerView.visibility = View.VISIBLE
        }
        contact.setOnClickListener {
            contact.alpha = 1.0f
            measuringPoints.alpha = 0.5f

            addPointsButton.visibility = View.GONE
            clientLiner.visibility = View.VISIBLE
            measurementsRecyclerView.visibility = View.GONE

        }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAllFields()
                finish()
            }
        })


        if (idDb != -1L) {
            val dbHelper = DbHelper(this, null)
            dbHelper.getClientDataEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameDb = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    clientName.editText?.setText(nameDb)
                    clientNameStatic.text = if (nameDb == "") "Имя клиента" else nameDb

                    val streetDb = cursor.getString(cursor.getColumnIndexOrThrow("street"))
                    clientStreet.editText?.setText(streetDb)

                    val cityDb = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                    clientCity.editText?.setText(cityDb)

                    val countryDb = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                    clientCountry.editText?.setText(countryDb)

                    val phoneDb = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
                    clientPhone.editText?.setText(phoneDb)

                    val emailDb = cursor.getString(cursor.getColumnIndexOrThrow("eMail"))
                    clientEmail.editText?.setText(emailDb)

                    val contactDb = cursor.getString(cursor.getColumnIndexOrThrow("contactPersons"))
                    clientContact.editText?.setText(contactDb)

                    val clientDb = cursor.getString(cursor.getColumnIndexOrThrow("customerData"))
                    clientData.editText?.setText(clientDb)
                }
            }
        }


        val focusListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val editText = view as? com.google.android.material.textfield.TextInputEditText
                if (editText == null) return@OnFocusChangeListener
                val inputText = editText.text.toString()

                val nameField = when ((editText.parent.parent as? TextInputLayout)?.id) {
                    R.id.client_name -> "name"
                    R.id.client_street -> "street"
                    R.id.client_city -> "city"
                    R.id.client_country -> "country"
                    R.id.client_phone -> "phoneNumber"
                    R.id.client_email -> "eMail"
                    R.id.client_contact -> "contactPersons"
                    R.id.client_data -> "customerData"
                    else -> null
                }
                if (nameField != null) {
                    val dbHelper = DbHelper(this, null)
                    dbHelper.updateClientData(idDb, nameField, inputText)
                }
                if (nameField == "name"){
                    clientNameStatic.text = inputText
                }

            }
        }

        clientName.editText?.onFocusChangeListener = focusListener
        clientStreet.editText?.onFocusChangeListener = focusListener
        clientCity.editText?.onFocusChangeListener = focusListener
        clientCountry.editText?.onFocusChangeListener = focusListener
        clientPhone.editText?.onFocusChangeListener = focusListener
        clientEmail.editText?.onFocusChangeListener = focusListener
        clientContact.editText?.onFocusChangeListener = focusListener
        clientData.editText?.onFocusChangeListener = focusListener




        val measurementsData = if (idDb != -1L){
            dbHelper.getMeasurementData(idDb)
        } else {
            arrayListOf()
        }
        measurementsAdapter = MeasurementsAdapter(
            measurementsData,
            onItemClicked = { clickedEntry ->
                val selectedId = clickedEntry.id
                val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MeasurementsDataActivity::class.java)
                startActivity(intent.putExtra("ID", selectedId))
            },
            onImageButtonClicked = {clickedEntry, position ->
                val toastText = "Вы нажали на запись от: ${clickedEntry.id}"
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()


                val newId = dbHelper.addMeasurement(clickedEntry, idDb)

                if (newId != -1L) {
                    val updatedMeasurements = dbHelper.getMeasurementData(idDb)
                    measurementsAdapter.updateData(updatedMeasurements)

                }



            }
        )

        measurementsRecyclerView.adapter = measurementsAdapter
        measurementsRecyclerView.layoutManager = LinearLayoutManager(this)

        addPointsButton.setOnClickListener {
            val measurement = MeasurementData()
            val newId = dbHelper.addMeasurement(measurement, idDb)

            if (newId != -1L) {
                val updatedMeasurements = dbHelper.getMeasurementData(idDb)
                measurementsAdapter.updateData(updatedMeasurements)

            }
        }
    }

    fun saveAllFields() {
        if (idDb == -1L) {
            return
        }

        val name = clientName.editText?.text.toString()
        val street = clientStreet.editText?.text.toString()
        val city = clientCity.editText?.text.toString()
        val country = clientCountry.editText?.text.toString()
        val phone = clientPhone.editText?.text.toString()
        val email = clientEmail.editText?.text.toString()
        val contact = clientContact.editText?.text.toString()
        val data = clientData.editText?.text.toString()

        val clientUpdates = mapOf(
            "name" to name,
            "street" to street,
            "city" to city,
            "country" to country,
            "phoneNumber" to phone,
            "eMail" to email,
            "contactPersons" to contact,
            "customerData" to data
        )

        clientUpdates.forEach { (fieldName, value) ->
            dbHelper.updateClientData(idDb, fieldName, value)
        }
    }

    override fun onResume() {
        super.onResume()
        val measurementsData = dbHelper.getMeasurementData(idDb)
        measurementsAdapter.updateData(measurementsData)
    }

}