package com.aegis.safetyalarm.data

import android.content.Context

class UserStorage(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val nameKey = "username"

    fun setName(name: String) {
        prefs.edit().putString(nameKey, name).apply()
    }

    fun getName(): String {
        return prefs.getString(nameKey, "").orEmpty()
    }

}