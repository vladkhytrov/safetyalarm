package com.aegis.safetyalarm

import android.content.Context
import android.telephony.SmsManager
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

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

    companion object {

        fun schedule(ctx: Context, sendAt: Long, inputData: Data): UUID {
            val diff = sendAt - Calendar.getInstance().timeInMillis
            val smsWorker = OneTimeWorkRequestBuilder<SmsWorker>()
                .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            val workManager = WorkManager.getInstance(ctx)
            workManager.enqueue(smsWorker)

            return smsWorker.id
        }

        fun cancelAllWorks(ctx: Context) {
            val workManager = WorkManager.getInstance(ctx)
            workManager.cancelAllWork()
        }

    }

}