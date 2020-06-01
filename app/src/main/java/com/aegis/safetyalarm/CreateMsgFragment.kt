package com.aegis.safetyalarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.work.*
import com.aegis.safetyalarm.data.ContactStorage
import com.aegis.safetyalarm.data.SmsStorage
import kotlinx.android.synthetic.main.fragment_create_msg.*
import kotlinx.android.synthetic.main.fragment_create_msg.btn_send
import kotlinx.android.synthetic.main.fragment_create_msg.btn_settings
import kotlinx.android.synthetic.main.fragment_create_msg.tv_date_time_create
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CreateMsgFragment : Fragment(R.layout.fragment_create_msg) {

    private var sendAt: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_settings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        btn_send.setOnClickListener {
            sendSMS()
        }

        tv_date_time_create.setOnClickListener {
            showDateTimePicker()
        }

        val smsStorage = SmsStorage(requireContext())
        val msg = smsStorage.getMsg()
        et_msg_create.setText(msg)
    }

    private fun sendSMS() {
        if (sendAt <= 0 || sendAt <= System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "You need to set date and time", Toast.LENGTH_SHORT).show()
            return
        }
        val contactStorage = ContactStorage(requireContext())
        val number = contactStorage.getContact()
        if (number.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "You need to set contact number", Toast.LENGTH_SHORT).show()
            return
        }
        val msg = et_msg_create.text.toString()
        val inputData: Data = workDataOf(
            "number" to number,
            "msg" to msg
        )

        val diff = sendAt - Calendar.getInstance().timeInMillis
        val smsWorker = OneTimeWorkRequestBuilder<SmsWorker>()
            .setInitialDelay(diff, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        val workId = smsWorker.id

        val smsStorage = SmsStorage(requireContext())
        smsStorage.saveWork(workId.toString())
        smsStorage.saveTime(sendAt)
        smsStorage.saveMsg(msg)

        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(smsWorker)
        (activity as MainActivity).showEditMsgFragment()
    }

    private fun showDateTimePicker() {
        val view = layoutInflater.inflate(R.layout.dialog_date_time, null)
        val datePicker: DatePicker = view.findViewById(R.id.datepicker)
        val timePicker: TimePicker = view.findViewById(R.id.timepicker)
        timePicker.setIs24HourView(true)
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
                sendAt = c.time.time
            }
            .create()
            .show()
    }

}