package com.aegis.safetyalarm

import android.app.Activity
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import java.text.DateFormat
import java.util.*

class DateTimeDialog(private val activity: Activity) {

    interface Callback {
        fun onPicked(calendar: Calendar)
    }

    fun show(callback: Callback) {
        val view = activity.layoutInflater.inflate(R.layout.dialog_date_time, null)
        val datePicker: DatePicker = view.findViewById(R.id.datepicker)
        val timePicker: TimePicker = view.findViewById(R.id.timepicker)
        timePicker.setIs24HourView(true)
        datePicker.minDate = Calendar.getInstance().timeInMillis

        AlertDialog.Builder(activity)
            .setView(view)
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Set") { dialog, which ->
                dialog.dismiss()
                val c = Calendar.getInstance()
                c.set(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth,
                    timePicker.currentHour,
                    timePicker.currentMinute
                )
                callback.onPicked(c)
            }
            .create()
            .show()
    }

}