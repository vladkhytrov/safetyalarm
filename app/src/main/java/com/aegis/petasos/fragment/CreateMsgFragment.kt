package com.aegis.petasos.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aegis.petasos.*
import com.aegis.petasos.activity.MainActivity
import com.aegis.petasos.data.SmsStorage
import com.google.android.material.textview.MaterialTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.fragment_create_msg.*
import java.util.*

class CreateMsgFragment : Fragment(R.layout.fragment_create_msg) {

    private var sendAt: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawLang()

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

    private fun sendSMS() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.SEND_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    showPassDialog()
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

    private fun showPassDialog() {
        PasswordDialog.showPassLock(requireActivity(), object : PasswordDialog.Callback {
            override fun onResult(success: Boolean) {
                val msg = et_msg_create.text.toString()
                (activity as MainActivity).sendSMS(sendAt, msg)
            }
        })
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