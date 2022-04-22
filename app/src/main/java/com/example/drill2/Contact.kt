package com.example.drill2

data class Contact(val name: String)

object ContactsManager {
    val contacts: MutableList<Contact> = mutableListOf()

    fun add(contact:Contact) {
        contacts.add(contact)
    }
}