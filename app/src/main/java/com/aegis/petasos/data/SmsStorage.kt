package com.aegis.petasos.data

import android.content.Context

/**
 * Class to store an active scheduled SMS message.
 */
class SmsStorage(ctx: Context) {

    private val prefs = ctx.getSharedPreferences("sms", Context.MODE_PRIVATE)
    private val workKey = "workId"
    private val timeKey = "time"
    private val msgKey = "msg"
    private val passKey = "pass"

    fun getWorkId(): String? {
        return prefs.getString(workKey, null)
    }

    fun saveWork(workId: String) {
        prefs.edit().putString(workKey, workId).apply()
    }

    fun deleteWork() {
        prefs.edit().putString(workKey, null).apply()
    }

    fun getTime(): Long {
        return prefs.getLong(timeKey, 0)
    }

    fun saveTime(time: Long) {
        prefs.edit().putLong(timeKey, time).apply()
    }

    fun getMsg(): String {
        return prefs.getString(msgKey, "").orEmpty()
    }

    fun saveMsg(msg: String) {
        prefs.edit().putString(msgKey, msg).apply()
    }

    fun getPass(): String {
        return prefs.getString(passKey, "").orEmpty()
    }

    fun savePass(pass: String) {
        prefs.edit().putString(passKey, pass).apply()
    }

    fun deletePass() {
        savePass("")
    }

}