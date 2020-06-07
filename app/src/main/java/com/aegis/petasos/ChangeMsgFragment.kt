package com.aegis.petasos

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aegis.petasos.viewmodel.SmsViewModel
import kotlinx.android.synthetic.main.fragment_change_msg.*
import java.util.*

class ChangeMsgFragment : Fragment(R.layout.fragment_change_msg) {

    private val smsViewModel by activityViewModels<SmsViewModel>()

    private val currentTime: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val time = smsViewModel.time.value!!
        val msg = smsViewModel.msg.value
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
            (activity as MainActivity).openSettings()
        }

        tv_date_time_change.setOnClickListener {
            showDateTimePicker()
        }

        btn_cancel_change.setOnClickListener {
            SmsWorker.cancelAllWorks(requireContext())
            val act = (activity as MainActivity)
            act.showToast(getString(R.string.msg_cancelled))
            act.checkActiveWork()
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