package com.aegis.safetyalarm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aegis.safetyalarm.data.db.Contact
import com.aegis.safetyalarm.data.db.AppDB

class ContactsViewModel(private val db: AppDB) : ViewModel() {

    class Factory(private val db: AppDB) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContactsViewModel(db) as T
        }

    }

    val emergencyContacts = MutableLiveData<List<Contact>>()
    val secondaryContacts = MutableLiveData<List<Contact>>()

    init {
        refresh()
    }

    private fun refresh() {
        emergencyContacts.value = db.contactDao().getEmergency()
        secondaryContacts.value = db.contactDao().getSecondary()
    }

    fun addContact(contact: Contact) {
        db.contactDao().insertContacts(contact)
        refresh()
    }

    fun deleteContact(contact: Contact) {
        db.contactDao().delete(contact)
        refresh()
    }

    fun deleteContactById(id: Int) {
        db.contactDao().deleteById(id)
        refresh()
    }

}