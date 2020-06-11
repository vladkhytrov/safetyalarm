package com.aegis.petasos.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aegis.petasos.App
import com.aegis.petasos.R
import com.aegis.petasos.SmsWorker
import com.aegis.petasos.data.SmsStorage
import com.aegis.petasos.data.UserStorage
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.formatted
import com.aegis.petasos.fragment.ChangeMsgFragment
import com.aegis.petasos.fragment.CreateMsgFragment
import com.aegis.petasos.fragment.SettingsFragment
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.SmsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*
import kotlin.collections.ArrayList

// todo permissions
class MainActivity : AppCompatActivity() {

    private val REQUEST_CHECK_SETTINGS = 1000

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

        val locationEnabled = userViewModel.locationEnabled.value!!
        if (locationEnabled) {
            requestEnableGPS()
        }

        val msgBuilder = StringBuilder()
            .append(getString(R.string.my_name_is))
            .append(" $username\n")
            .append(getString(R.string.my_preset_time))
            .append(" $dateText\n")
            .append("$msg\n")
            .append("${getString(R.string.emergency_contact)}\n")

        emContacts.iterator().forEach {
            msgBuilder.append("${it.name} ${getString(R.string.phone)} ${it.number}\n")
        }

        val msgToSend = msgBuilder.toString()
        val allContacts = ArrayList<Contact>()
        allContacts.addAll(emContacts)
        allContacts.addAll(secContacts)

        val inputData = Data.Builder()
            .putBoolean("locationEnabled", locationEnabled)
            .putString("msg", msgToSend)
            .putStringArray("contacts", contactsToStringArray(allContacts).toTypedArray())
            .build()

        val workId = SmsWorker.schedule(
            this,
            sendAt,
            inputData
        )

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
                        SmsStorage(this).deletePass()
                        showCreateMsgFragment()
                    }
                }
            })
    }

    private fun requestEnableGPS() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode != Activity.RESULT_OK) {
                // todo
            }
        }
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
            .add(
                R.id.frame,
                SettingsFragment()
            )
            .addToBackStack(null)
            .commit()
    }

    fun checkActiveWork() {
        val workId = smsViewModel.workId.value
        if (workId != null) {
            val workManager = WorkManager.getInstance(this)
            val workInfo = workManager.getWorkInfoById(UUID.fromString(workId)).get()
            if (workInfo != null && (workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED)) {
                showEditMsgFragment()
            } else {
                SmsStorage(this).deletePass()
                smsViewModel.deleteWork()
                showCreateMsgFragment()
            }
        } else {
            showCreateMsgFragment()
        }
    }

    private fun showCreateMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                CreateMsgFragment()
            )
            .commit()
    }

    private fun showEditMsgFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                ChangeMsgFragment()
            )
            .commit()
    }

}