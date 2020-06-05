package com.aegis.safetyalarm

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.aegis.safetyalarm.data.SmsStorage
import kotlinx.android.synthetic.main.fragment_create_msg.*
import java.util.*

class CreateMsgFragment : Fragment(R.layout.fragment_create_msg) {

    private var sendAt: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendAt = Calendar.getInstance().apply {
            add(Calendar.HOUR, 1)
        }.timeInMillis

        tv_date_time_create.text = sendAt.formatted()

        view_settings_create.findViewById<ImageButton>(R.id.btn_settings).setOnClickListener {
            (activity as MainActivity).openSettings()
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
        val msg = et_msg_create.text.toString()
        (activity as MainActivity).sendSMS(sendAt, msg)
    }

    private fun showDateTimePicker() {
        DateTimeDialog(requireActivity()).show(object : DateTimeDialog.Callback {
            override fun onPicked(calendar: Calendar) {
                tv_date_time_create.text = calendar.timeInMillis.formatted()
                sendAt = calendar.timeInMillis
            }
        })
    }

}