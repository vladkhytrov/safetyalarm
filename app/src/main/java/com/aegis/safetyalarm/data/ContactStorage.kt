package com.aegis.safetyalarm.data

import android.content.Context

class ContactStorage(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("aegis", Context.MODE_PRIVATE)
    private val contactKey = "contact"

    fun setContact(number: String) {
        prefs.edit().putString(contactKey, number).apply()
    }

    fun getContact(): String {
        return prefs.getString(contactKey, "").orEmpty()
    }

    fun deleteContact(index: Int) {
        prefs.edit().remove(contactKey).apply()
    }

}