package com.aegis.petasos.data

import android.content.Context

class UserStorage(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val nameKey = "username"
    private val locationKey = "location"

    fun setName(name: String) {
        prefs.edit().putString(nameKey, name).apply()
    }

    fun getName(): String {
        return prefs.getString(nameKey, "").orEmpty()
    }

    fun isLocationEnabled(): Boolean {
        return prefs.getBoolean(locationKey, false)
    }

    fun setLocationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(locationKey, enabled).apply()
    }

}