package com.aegis.petasos.data

import android.content.Context

class UserStorage(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val nameKey = "username"
    private val locationKey = "location"
    private val firstLaunchKey = "firstLaunch"
    private val langKey = "langKey"

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

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(firstLaunchKey, true)
    }

    fun setFirstLaunch(first: Boolean) {
        prefs.edit().putBoolean(firstLaunchKey, first).apply()
    }

    fun getLang(): String {
        return prefs.getString(langKey, "").orEmpty()
    }

    fun setLang(lang: String) {
        prefs.edit().putString(langKey, lang).apply()
    }

}