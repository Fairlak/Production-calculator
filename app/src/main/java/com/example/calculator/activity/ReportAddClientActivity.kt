package com.example.calculator.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReportAddClientActivity : AppCompatActivity() {

    private val dbHelper by lazy { DbHelper(this, null) }

    private var idDb: Long = -1L
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

        setContentView(R.layout.activity_report_add_client)


        val comeBack: ImageButton = findViewById(R.id.back_to_report_clients_button)


        clientNameStatic = findViewById(R.id.add_client_name_static)

        clientName = findViewById(R.id.add_client_name)
        clientStreet = findViewById(R.id.add_client_street)
        clientCity = findViewById(R.id.add_client_city)
        clientCountry = findViewById(R.id.add_client_country)
        clientPhone = findViewById(R.id.add_client_phone)
        clientEmail = findViewById(R.id.add_client_email)
        clientContact = findViewById(R.id.add_client_contact)
        clientData = findViewById(R.id.add_client_data)



        idDb = intent.getLongExtra("ID", -1L)



        comeBack.setOnClickListener {
            saveAllFields()
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
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
                val editText = view as? TextInputEditText
                if (editText == null) return@OnFocusChangeListener
                val inputText = editText.text.toString()

                val nameField = when ((editText.parent.parent as? TextInputLayout)?.id) {
                    R.id.add_client_name -> "name"
                    R.id.add_client_street -> "street"
                    R.id.add_client_city -> "city"
                    R.id.add_client_country -> "country"
                    R.id.add_client_phone -> "phoneNumber"
                    R.id.add_client_email -> "eMail"
                    R.id.add_client_contact -> "contactPersons"
                    R.id.add_client_data -> "customerData"
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

}