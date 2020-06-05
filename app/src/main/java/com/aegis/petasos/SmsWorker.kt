package com.aegis.petasos

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
        val msg = params.inputData.getString("msg")
        val contacts = params.inputData.getStringArray("contacts")

        val smsManager = SmsManager.getDefault()
        contacts?.forEach {
            smsManager.sendTextMessage(it, null, msg, null, null)
        }
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