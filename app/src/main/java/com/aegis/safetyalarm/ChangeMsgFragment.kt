package com.aegis.safetyalarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import com.aegis.safetyalarm.data.SmsStorage
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_change_msg.*
import kotlinx.android.synthetic.main.fragment_create_msg.*
import java.text.DateFormat
import java.util.*

class ChangeMsgFragment : Fragment(R.layout.fragment_change_msg) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smsStorage = SmsStorage(requireContext())
        val time = smsStorage.getTime()
        val msg = smsStorage.getMsg()
        et_msg_edit.setText(msg)

        val formatted = DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
        ).format(Date(time))
        tv_date_time_change.text = formatted

        view_settings.findViewById<MaterialButton>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        tv_date_time_change.setOnClickListener {
            showDateTimePicker()
        }

        btn_cancel_change.setOnClickListener {
            val workManager = WorkManager.getInstance(requireContext())
            workManager.cancelAllWork()
            (activity as MainActivity).checkActiveWork()
        }
    }

    // todo extract to class
    private fun showDateTimePicker() {
        val view = layoutInflater.inflate(R.layout.dialog_date_time, null)
        val datePicker: DatePicker = view.findViewById(R.id.datepicker)
        val timePicker: TimePicker = view.findViewById(R.id.timepicker)
        datePicker.minDate = Calendar.getInstance().timeInMillis

        AlertDialog.Builder(requireActivity())
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
                val formatted = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT
                ).format(c.time)
                tv_date_time_create.text = formatted
            }
            .create()
            .show()
    }

}