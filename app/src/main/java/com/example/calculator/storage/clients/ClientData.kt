package com.example.calculator.storage.clients

data class ClientData(
    val id: Long = 0,
    val name: String = "",
    val street: String = "",
    val city: String = "",
    val country: String = "",
    val phoneNumber: String = "",
    val eMail: String = "",
    val contactPersons: String = "",
    val customerData: String = ""
)
