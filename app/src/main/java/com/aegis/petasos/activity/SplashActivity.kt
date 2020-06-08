package com.aegis.petasos.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aegis.petasos.R
import com.aegis.petasos.data.UserStorage

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Thread(Runnable {
            try {
                Thread.sleep(800)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val userStorage = UserStorage(this)
            if (userStorage.isFirstLaunch()) {
                startActivity(
                    Intent(this, IntroActivity::class.java)
                )
                finish()
            } else {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                finish()
            }
        }).start()
    }

}