package com.aegis.petasos.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aegis.petasos.R
import com.aegis.petasos.adapter.IntroAdapter
import com.aegis.petasos.fragment.intro.*
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnBoardingActivity : AppCompatActivity() {

    private var introPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            //updateCircleMarker(binding, position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val introScreens = listOf(
            FragmentWelcome(),
            DescriptionFragment(),
            LocationFragment(),
            ContactsFragment(),
            NameFragment()
        )

        val introAdapter = IntroAdapter(this, introScreens)
        pager.adapter = introAdapter
        pager.registerOnPageChangeCallback(introPageChangeCallback)
    }

}