package com.aegis.petasos

import android.content.Context
import android.telephony.SmsManager
import androidx.work.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import java.util.*
import java.util.concurrent.TimeUnit

class SmsWorker(
    private val context: Context,
    private val params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        var locationText = ""
        val locationEnabled = params.inputData.getBoolean("locationEnabled", false)
        if (locationEnabled) {
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = Tasks.await(locationClient.lastLocation)
            if (location != null) {
                val lat = location.latitude
                val long = location.longitude
                locationText = "Last location: https://www.google.com/maps/search/?api=1&query=$lat,$long"
            }
        }
        var msg = params.inputData.getString("msg")
        val contacts = params.inputData.getStringArray("contacts")
        if (locationText.isNotEmpty()) {
            msg += locationText
        }
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