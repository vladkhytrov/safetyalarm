package com.aegis.petasos.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.aegis.petasos.R
import com.aegis.petasos.adapter.IntroAdapter
import com.aegis.petasos.data.UserStorage
import com.aegis.petasos.fragment.intro.*
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnBoardingActivity : AppCompatActivity() {

    private var introPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateIndicator(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val nameFragment = NameFragment()
        val introScreens = listOf(
            FragmentWelcome(),
            DescriptionFragment(),
            LocationFragment(),
            ContactsFragment(),
            nameFragment
        )

        val introAdapter = IntroAdapter(this, introScreens)
        pager.adapter = introAdapter
        pager.registerOnPageChangeCallback(introPageChangeCallback)

        btn_intro_back.setOnClickListener {
            var pos = pager.currentItem
            pos--
            if (pos >= 0) {
                pager.setCurrentItem(pos, true)
            }
        }
        btn_intro_continue.setOnClickListener {
            var pos = pager.currentItem
            pos++
            if (pos <= (introScreens.size - 1)) {
                pager.setCurrentItem(pos, true)
            } else {
                if (nameFragment.validateName()) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                    UserStorage(this).setFirstLaunch(false)
                }
            }
        }

        val r = Runnable { updateIndicator(0) }
        Thread(Runnable {
            Handler(Looper.getMainLooper()).postDelayed(r, 100)
        }).start()
    }

    private fun updateIndicator(activePosition: Int) {
        val dots = arrayOfNulls<ImageView>(5)
        val dotActive = ContextCompat.getDrawable(this, R.drawable.dot_active)
        val dotInactive = ContextCompat.getDrawable(this, R.drawable.dot_inactive)
        val margin = resources.getDimension(R.dimen.dot_margin).toInt()
        val dotSize = LinearLayout.LayoutParams.WRAP_CONTENT
        layout_dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            params.setMargins(margin, 0, margin, 0)
            if (i != activePosition) {
                dots[i]?.background = dotInactive
            } else {
                dots[i]?.background = dotActive
            }
            dots[i]?.left = 10
            layout_dots.addView(dots[i], params)
        }
//        if (activePosition != (items.size - 1)) {
//            btn_skip.visibility = View.VISIBLE
//        } else {
//            btn_skip.visibility = View.INVISIBLE
//        }
    }

}