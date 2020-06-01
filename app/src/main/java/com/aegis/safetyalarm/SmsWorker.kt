package com.aegis.safetyalarm

import android.content.Context
import android.telephony.SmsManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class SmsWorker(
    context: Context,
    private val params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val number = params.inputData.getString("number")
        val msg = params.inputData.getString("msg")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(number, null, msg, null, null)
        return Result.success()
    }

}