package com.example.calculator.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.google.android.material.textfield.TextInputLayout

class ClientDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_data)

        val idDb = intent.getLongExtra("ID", -1L)


        val clientName: TextInputLayout = findViewById(R.id.client_name)
        val clientStreet: TextInputLayout = findViewById(R.id.client_street)
        val clientCity: TextInputLayout = findViewById(R.id.client_city)
        val clientCountry: TextInputLayout = findViewById(R.id.client_country)
        val clientPhone: TextInputLayout = findViewById(R.id.client_phone)
        val clientEmail: TextInputLayout = findViewById(R.id.client_email)
        val clientContact: TextInputLayout = findViewById(R.id.client_contact)
        val clientData: TextInputLayout = findViewById(R.id.client_data)



        if (idDb != -1L) {
            val dbHelper = DbHelper(this, null)
            dbHelper.getClientDataEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {

                    val nameDb = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    clientName.editText?.setText(nameDb.toString())

                    val streetDb = cursor.getString(cursor.getColumnIndexOrThrow("street"))
                    clientStreet.editText?.setText(streetDb.toString())

                    val cityDb = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                    clientCity.editText?.setText(cityDb.toString())

                    val countryDb = cursor.getString(cursor.getColumnIndexOrThrow("country"))
                    clientCountry.editText?.setText(countryDb.toString())

                    val phoneDb = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
                    clientPhone.editText?.setText(phoneDb.toString())

                    val emailDb = cursor.getString(cursor.getColumnIndexOrThrow("eMail"))
                    clientEmail.editText?.setText(emailDb.toString())

                    val contactDb = cursor.getString(cursor.getColumnIndexOrThrow("contactPersons"))
                    clientContact.editText?.setText(contactDb.toString())

                    val clientDb = cursor.getString(cursor.getColumnIndexOrThrow("customerData"))
                    clientData.editText?.setText(clientDb.toString())
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
}