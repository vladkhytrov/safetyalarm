package com.aegis.petasos.data

import android.content.Context

abstract class ContactsStorage(ctx: Context, prefName: String) {

    private val prefs = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val numberKey = "number"
    private val nameKey = "name"

    fun setNumber(number: String) {
        prefs.edit().putString(numberKey, number).apply()
    }

    fun getNumber(): String {
        return prefs.getString(numberKey, "").orEmpty()
    }

    fun setName(name: String) {
        prefs.edit().putString(nameKey, name).apply()
    }

    fun getName(): String {
        return prefs.getString(nameKey, "").orEmpty()
    }

    fun deleteContact() {
        prefs.edit()
            .remove(numberKey)
            .remove(nameKey)
            .apply()
    }

}