package com.aegis.safetyalarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.aegis.safetyalarm.data.SmsStorage
import kotlinx.android.synthetic.main.fragment_change_msg.*
import java.util.*

class ChangeMsgFragment : Fragment(R.layout.fragment_change_msg) {

    private val currentTime: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smsStorage = SmsStorage(requireContext())
        val time = smsStorage.getTime()
        val msg = smsStorage.getMsg()
        et_msg_edit.setText(msg)
        setFormattedTime(time)

        currentTime.timeInMillis = time

        btn_15_mins.setOnClickListener {
            currentTime.add(Calendar.MINUTE, 15)
            setFormattedTime(currentTime.timeInMillis)
        }
        btn_30_mins.setOnClickListener {
            currentTime.add(Calendar.MINUTE, 30)
            setFormattedTime(currentTime.timeInMillis)
        }
        btn_1_hour.setOnClickListener {
            currentTime.add(Calendar.HOUR, 1)
            setFormattedTime(currentTime.timeInMillis)
        }
        btn_2_hours.setOnClickListener {
            currentTime.add(Calendar.HOUR, 2)
            setFormattedTime(currentTime.timeInMillis)
        }

        view_settings_change.findViewById<ImageButton>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        tv_date_time_change.setOnClickListener {
            showDateTimePicker()
        }

        btn_cancel_change.setOnClickListener {
            SmsWorker.cancelAllWorks(requireContext())
            (activity as MainActivity).checkActiveWork()
        }
        btn_reset.setOnClickListener {
            SmsWorker.cancelAllWorks(requireContext())
            val newMsg = et_msg_edit.text.toString()
            (activity as MainActivity).sendSMS(currentTime.timeInMillis, newMsg, reset = true)
        }
    }

    private fun setFormattedTime(millis: Long) {
        tv_date_time_change.text = millis.formatted()
    }

    private fun showDateTimePicker() {
        DateTimeDialog(requireActivity()).show(object : DateTimeDialog.Callback {
            override fun onPicked(calendar: Calendar) {
                currentTime.timeInMillis = calendar.timeInMillis
                tv_date_time_change.text = currentTime.timeInMillis.formatted()
            }
        })

    }

}