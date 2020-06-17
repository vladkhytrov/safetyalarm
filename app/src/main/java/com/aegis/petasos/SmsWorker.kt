package com.aegis.petasos

import android.content.Context
import android.os.Bundle
import android.telephony.SmsManager
import androidx.work.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
                locationText =
                    "${context.getString(R.string.last_location)} https://www.google.com/maps/search/?api=1&query=$lat,$long"
            }
        }
        var msg = params.inputData.getString("msg")
        val contacts = params.inputData.getStringArray("contacts")

        val smsManager = SmsManager.getDefault()
        contacts?.forEach {
            try {
                if (locationText.isNotEmpty()) {
                    msg += locationText
                }
                val parts = smsManager.divideMessage(msg)
                smsManager.sendMultipartTextMessage(it, null, parts, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id.toString())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "sms")
        bundle.putString(Analytics.Param.SENT_AT, Calendar.getInstance().timeInMillis.toString())
        FirebaseAnalytics.getInstance(context).logEvent(Analytics.Event.SMS_SENT, bundle)

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