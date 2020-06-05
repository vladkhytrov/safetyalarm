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

    fun getWorkId(): String? {
        return prefs.getString(workKey, null)
    }

    fun saveWork(workId: String) {
        prefs.edit().putString(workKey, workId).apply()
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

}