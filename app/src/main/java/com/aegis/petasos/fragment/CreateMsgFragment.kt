package com.aegis.petasos.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aegis.petasos.DateTimeDialog
import com.aegis.petasos.R
import com.aegis.petasos.activity.MainActivity
import com.aegis.petasos.data.SmsStorage
import com.aegis.petasos.formatted
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_create_msg.*
import java.util.*

class CreateMsgFragment : Fragment(R.layout.fragment_create_msg) {

    private var sendAt: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(
                R.id.create_msg_settings_container,
                SettingsWidgetFragment()
            )
            .commit()

        sendAt = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }.timeInMillis

        tv_date_time_create.text = sendAt.formatted()

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
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.SEND_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    val msg = et_msg_create.text.toString()
                    (activity as MainActivity).sendSMS(sendAt, msg)
                }

                override fun onPermissionRationaleShouldBeShown(
                    request: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    // todo
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    // todo
                }

            })
            .check()
    }

    private fun showDateTimePicker() {
        DateTimeDialog(requireActivity())
            .show(object : DateTimeDialog.Callback {
            override fun onPicked(calendar: Calendar) {
                tv_date_time_create.text = calendar.timeInMillis.formatted()
                sendAt = calendar.timeInMillis
            }
        })
    }

}