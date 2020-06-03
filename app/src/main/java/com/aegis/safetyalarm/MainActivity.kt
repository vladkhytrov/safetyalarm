package com.aegis.safetyalarm

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.aegis.safetyalarm.data.ContactStorage
import com.aegis.safetyalarm.data.SmsStorage
import java.util.*

// todo permissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 100)

        checkActiveWork()
    }

    fun sendSMS(sendAt: Long, msg: String, reset: Boolean = false) {
        if (sendAt <= 0 || sendAt <= System.currentTimeMillis()) {
            Toast.makeText(this, "You need to set date and time", Toast.LENGTH_SHORT).show()
            return
        }
        val contactStorage = ContactStorage(this)
        val number = contactStorage.getContact()
        if (number.isEmpty()) {
            Toast.makeText(this, "You need to set contact number", Toast.LENGTH_SHORT).show()
            return
        }

        val inputData: Data = workDataOf(
            "number" to number,
            "msg" to msg
        )

        val workId = SmsWorker.schedule(this, sendAt, inputData)

        val smsStorage = SmsStorage(this)
        smsStorage.saveWork(workId.toString())
        smsStorage.saveTime(sendAt)
        smsStorage.saveMsg(msg)

        if (!reset) {
            showEditMsgFragment()
        }

        val workManager = WorkManager.getInstance(this)
        workManager.getWorkInfoByIdLiveData(workId)
            .observe(this, androidx.lifecycle.Observer { workInfo ->
                workInfo?.let {
                    if (it.state.isFinished && it.state != WorkInfo.State.CANCELLED) {
                        showCreateMsgFragment()
                    }
                }
            })
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

    private fun showCreateMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, CreateMsgFragment())
            .commit()
    }

    private fun showEditMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, ChangeMsgFragment())
            .commit()
    }

}