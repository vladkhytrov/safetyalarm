package com.aegis.safetyalarm

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aegis.safetyalarm.data.SmsStorage
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 100)

        checkActiveWork()
    }

    fun checkActiveWork() {
        val smsStorage = SmsStorage(this)
        val workId = smsStorage.getWorkId()
        if (workId != null) {
            val workManager = WorkManager.getInstance(this)
            val workInfo = workManager.getWorkInfoById(UUID.fromString(workId)).get()
            if (workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED) {
                showEditMsgFragment()
            }
        }
        // todo delete work from storage
        showCreateMsgFragment()
    }

    fun showCreateMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, CreateMsgFragment())
            .commit()
    }

    fun showEditMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, ChangeMsgFragment())
            .commit()
    }

}