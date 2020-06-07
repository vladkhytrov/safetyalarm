package com.aegis.petasos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aegis.petasos.data.SmsStorage
import com.aegis.petasos.data.UserStorage
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.SmsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import java.util.*
import kotlin.collections.ArrayList

// todo permissions
class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var smsViewModel: SmsViewModel

    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel = ViewModelProvider(
            this,
            UserViewModel.Factory(UserStorage(this))
        ).get(UserViewModel::class.java)
        smsViewModel = ViewModelProvider(
            this,
            SmsViewModel.Factory(SmsStorage(this))
        ).get(SmsViewModel::class.java)
        contactsViewModel = ViewModelProvider(
            this,
            ContactsViewModel.Factory(
                (application as App).db
            )
        ).get(ContactsViewModel::class.java)

        checkActiveWork()
    }

    fun sendSMS(sendAt: Long, msg: String, reset: Boolean = false) {
        if (sendAt <= 0 || sendAt <= System.currentTimeMillis()) {
            showToast(getString(R.string.validation_date))
            return
        }
        val dateText = sendAt.formatted()

        val username = userViewModel.username.value
        if (username.isNullOrEmpty()) {
            showToast(getString(R.string.validation_name))
            return
        }

        val emContacts = contactsViewModel.emergencyContacts.value.orEmpty()
        val secContacts = contactsViewModel.secondaryContacts.value.orEmpty()
        if (emContacts.isNullOrEmpty() && secContacts.isNullOrEmpty()) {
            showToast(getString(R.string.validation_contacts))
            return
        }

        val msgBuilder = StringBuilder()
            .append("My name is ")
            .append("$username\n")
            .append("My present time to safety is: ")
            .append("$dateText\n")
            .append("$msg\n")
            .append("Emergency contact(s):\n")

        emContacts.iterator().forEach {
            msgBuilder.append("${it.name} Phone: ${it.number}\n")
        }

        val msgToSend = msgBuilder.toString()
        val allContacts = ArrayList<Contact>()
        allContacts.addAll(emContacts)
        allContacts.addAll(secContacts)

        val inputData = Data.Builder()
            .putString("msg", msgToSend)
            .putStringArray("contacts", contactsToStringArray(allContacts).toTypedArray())
            .build()

        val workId = SmsWorker.schedule(this, sendAt, inputData)

        smsViewModel.setWork(workId.toString(), sendAt, msg)

        if (!reset) {
            showEditMsgFragment()
            showToast(getString(R.string.msg_created))
        } else {
            showToast(getString(R.string.msg_changed))
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

    /**
     * Converts each contact to string array so that it can be passed to WorkManager
     */
    private fun contactsToStringArray(contacts: List<Contact>): List<String> {
        val result = mutableListOf<String>()
        contacts.iterator().forEach {
            result.add(it.number)
        }
        return result
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun openSettings() {
        supportFragmentManager.beginTransaction()
            .add(R.id.frame, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    fun checkActiveWork() {
        val workId = smsViewModel.workId.value
        if (workId != null) {
            val workManager = WorkManager.getInstance(this)
            val workInfo = workManager.getWorkInfoById(UUID.fromString(workId)).get()
            if (workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED) {
                showEditMsgFragment()
            } else {
                smsViewModel.deleteWork()
                showCreateMsgFragment()
            }
        } else {
            showCreateMsgFragment()
        }
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