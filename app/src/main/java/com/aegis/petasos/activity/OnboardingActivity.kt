package com.aegis.petasos.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aegis.petasos.R

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

//        updateIndicator(0)
//        view_pager_intro.adapter = ViewPagerAdapter(this)
//        view_pager_intro.addOnPageChangeListener(pageChangeListener)
    }

    /*private val items = 3

        private val pageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                updateIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        updateIndicator(0)
        view_pager_intro.adapter = ViewPagerAdapter(this)
        view_pager_intro.addOnPageChangeListener(pageChangeListener)
    }

    private fun updateIndicator(activePosition: Int) {
        val dots = arrayOfNulls<ImageView>(items.size)
        val dotActive = ContextCompat.getDrawable(this, R.drawable.bg_intro_dot_active)
        val dotInactive = ContextCompat.getDrawable(this, R.drawable.bg_intro_dot_inactive)
        val margin = resources.getDimension(R.dimen.margin_2xs).toInt()
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
        if (activePosition != (items.size - 1)) {
            btn_skip.visibility = View.VISIBLE
        } else {
            btn_skip.visibility = View.INVISIBLE
        }
    }

    inner class ViewPagerAdapter(ctx: Context) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(items[position])
            return items[position]
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val v = `object` as View
            container.removeView(v)
        }
    }*/

}
