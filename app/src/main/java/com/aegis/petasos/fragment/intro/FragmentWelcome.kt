package com.aegis.petasos.fragment.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aegis.petasos.R
import com.aegis.petasos.activity.OnBoardingActivity
import com.aegis.petasos.setImg
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.fragment_intro_welcome.*
import java.util.*

class FragmentWelcome : Fragment(R.layout.fragment_intro_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawLang()
    }

    private fun drawLang() {
        val lingver = Lingver.getInstance()
        img_eng.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.ENGLISH)
            val i = Intent(requireContext(), OnBoardingActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        img_ch_tr.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.TRADITIONAL_CHINESE)
            val i = Intent(requireContext(), OnBoardingActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        img_ch_sim.setOnClickListener {
            lingver.setLocale(requireContext(), Locale.SIMPLIFIED_CHINESE)
            val i = Intent(requireContext(), OnBoardingActivity::class.java)
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

}