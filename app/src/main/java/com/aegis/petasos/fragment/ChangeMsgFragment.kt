package com.aegis.petasos.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aegis.petasos.*
import com.aegis.petasos.activity.MainActivity
import com.aegis.petasos.viewmodel.SmsViewModel
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.fragment_change_msg.*
import java.util.*

class ChangeMsgFragment : Fragment(R.layout.fragment_change_msg) {

    private val smsViewModel by activityViewModels<SmsViewModel>()
    private val currentTime: Calendar = Calendar.getInstance()

    private var delayMinutes = 15

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawLang()

        val time = smsViewModel.time.value!!
        val msg = smsViewModel.msg.value
        tv_msg.text = msg
        setFormattedTime(time)

        currentTime.timeInMillis = time

//        btn_15_mins.setOnClickListener {
//            currentTime.add(Calendar.MINUTE, 15)
//            setFormattedTime(currentTime.timeInMillis)
//        }
//        btn_30_mins.setOnClickListener {
//            currentTime.add(Calendar.MINUTE, 30)
//            setFormattedTime(currentTime.timeInMillis)
//        }
//        btn_1_hour.setOnClickListener {
//            currentTime.add(Calendar.HOUR, 1)
//            setFormattedTime(currentTime.timeInMillis)
//        }
//        btn_2_hours.setOnClickListener {
//            currentTime.add(Calendar.HOUR, 2)
//            setFormattedTime(currentTime.timeInMillis)
//        }

        childFragmentManager.beginTransaction()
            .replace(
                R.id.change_msg_settings_container,
                SettingsWidgetFragment()
            )
            .commit()

//        tv_date_time_change.setOnClickListener {
//            showDateTimePicker()
//        }

        btn_cancel_change.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.dialog_cancel_msg)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    cancelMsg()
                }
                .create()
                .show()

        }
//        btn_reset.setOnClickListener {
//            resetMsg()
//        }

        btn_minus.setOnClickListener {
            minusDelay()
        }
        btn_plus.setOnClickListener {
            plusDelay()
        }
        btn_delay.setOnClickListener {
            currentTime.add(Calendar.MINUTE, delayMinutes)
            resetMsg()
        }

        refreshDelay()
    }

    private fun minusDelay() {
        btn_plus.isEnabled = true
        delayMinutes = delayMinutes.minus(15)
        if (delayMinutes <= 15) {
            delayMinutes = 15
            btn_minus.isEnabled = false
        }
        refreshDelay()
    }

    private fun plusDelay() {
        btn_minus.isEnabled = true
        delayMinutes = delayMinutes.plus(15)
        if (delayMinutes >= 120) {
            delayMinutes = 120
            btn_plus.isEnabled = false
        }
        refreshDelay()
    }

    private fun refreshDelay() {
        val hours: Int = delayMinutes / 60
        val minutes: Int = delayMinutes % 60

        var hr = if (hours > 0) {
            if (hours > 1) {
                "$hours ${getString(R.string.hours)} "
            } else {
                "$hours ${getString(R.string.hour)} "
            }
        } else {
            ""
        }
        val mins = if (minutes > 0) {
            "$minutes ${getString(R.string.minutes)}"
        } else {
            ""
        }
        val formatted = "${getString(R.string.delay_by)} $hr$mins"
        tv_delay.text = formatted
    }

    private fun drawLang() {
        val lingver = Lingver.getInstance()
        img_eng.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.ENGLISH)
            val i = Intent(requireContext(), MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        img_ch_tr.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.TRADITIONAL_CHINESE)
            val i = Intent(requireContext(), MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        img_ch_sim.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.SIMPLIFIED_CHINESE)
            val i = Intent(requireContext(), MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        val locale = Lingver.getInstance().getLocale()
        val eng = locale != Locale.TRADITIONAL_CHINESE && locale != Locale.SIMPLIFIED_CHINESE
        if (eng) {
            img_eng.setImg(R.drawable.eng_on)
            img_eng.isClickable = false
            img_ch_tr.setImg(R.drawable.tchin_off)
            img_ch_sim.setImg(R.drawable.schin_off)
        } else {
            img_eng.setImg(R.drawable.eng_off)
            if (locale == Locale.TRADITIONAL_CHINESE) {
                img_ch_tr.setImg(R.drawable.tchin_on)
                img_ch_tr.isClickable = false
                img_ch_sim.setImg(R.drawable.schin_off)
            } else {
                img_ch_tr.setImg(R.drawable.tchin_off)
                img_ch_sim.setImg(R.drawable.schin_on)
                img_ch_sim.isClickable = false
            }
        }
    }

    private fun cancelMsg() {
        SmsWorker.cancelAllWorks(requireContext())
        val act = (activity as MainActivity)
        act.showToast(getString(R.string.msg_cancelled))
        act.checkActiveWork()
    }

    private fun resetMsg() {
        SmsWorker.cancelAllWorks(requireContext())
        val newMsg = tv_msg.text.toString()
        val reset = (activity as MainActivity).sendSMS(currentTime.timeInMillis, newMsg, reset = true)
        if (reset) {
            delayMinutes = 15
            refreshDelay()
            tv_date_time_change.text = currentTime.timeInMillis.formatted()
        }
    }

    private fun setFormattedTime(millis: Long) {
        tv_date_time_change.text = millis.formatted()
    }

    private fun showDateTimePicker() {
        DateTimeDialog(requireActivity())
            .show(object : DateTimeDialog.Callback {
                override fun onPicked(calendar: Calendar) {
                    currentTime.timeInMillis = calendar.timeInMillis
                    tv_date_time_change.text = currentTime.timeInMillis.formatted()
                }
            })

    }

}