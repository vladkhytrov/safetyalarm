package com.aegis.petasos.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aegis.petasos.App
import com.aegis.petasos.R
import com.aegis.petasos.data.UserStorage
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import com.google.android.material.chip.Chip
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.chip_group_em
import kotlinx.android.synthetic.main.fragment_settings.chip_group_sec
import kotlinx.android.synthetic.main.fragment_settings.et_username
import kotlinx.android.synthetic.main.fragment_settings.location_switch

class IntroActivity : AppCompatActivity() {

    private val RQ_CODE_EMERGENCY = 1
    private val RQ_CODE_SECONDARY = 2

    private lateinit var userViewModel: UserViewModel
    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        btn_continue.setOnClickListener {
            val username = et_username_intro.text.toString().trim()
            if (username.isEmpty()) {
                username_layout.error = getString(R.string.validation_name)
            } else {
                userViewModel.setUsername(username)
                UserStorage(this).setFirstLaunch(false)
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                finish()
            }
        }

        et_username_intro.doAfterTextChanged {
            username_layout.error = null
        }

        userViewModel = ViewModelProvider(
            this,
            UserViewModel.Factory(UserStorage(this))
        ).get(UserViewModel::class.java)
        contactsViewModel = ViewModelProvider(
            this,
            ContactsViewModel.Factory(
                (application as App).db
            )
        ).get(ContactsViewModel::class.java)

        location_switch_intro.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                Dexter.withContext(this)
                    .withPermissions(permissions)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(result: MultiplePermissionsReport) {
                            if (result.areAllPermissionsGranted()) {
                                userViewModel.setLocationEnabled(true)
                            } else {
                                userViewModel.setLocationEnabled(false)
                                location_switch_intro.isChecked = false
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            request: MutableList<PermissionRequest>,
                            token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }

                    })
                    .check()
            } else {
                userViewModel.setLocationEnabled(false)
            }
        }

        userViewModel.username.observe(this, Observer {
            et_username_intro.setText(it)
        })

        userViewModel.locationEnabled.observe(this, Observer {
            location_switch_intro.isChecked = it
        })

        contactsViewModel.emergencyContacts.observe(this, Observer {
            renderEmContacts(it)
        })

        contactsViewModel.secondaryContacts.observe(this, Observer {
            renderSecContacts(it)
        })
    }

    private fun renderEmContacts(contacts: List<Contact>) {
        chip_group_em_intro.removeAllViews()
        contacts.forEach {
            chip_group_em_intro.addView(getChipForContact(it))
        }
        val addChip = createAddContactChip()
        addChip.setOnClickListener { v ->
            val intent = Intent(this, PickContactActivity::class.java)
            startActivityForResult(intent, RQ_CODE_EMERGENCY)
        }
        chip_group_em_intro.addView(addChip)
    }

    private fun renderSecContacts(contacts: List<Contact>) {
        chip_group_sec_intro.removeAllViews()
        contacts.forEach {
            chip_group_sec_intro.addView(getChipForContact(it))
        }
        val addChip = createAddContactChip()
        addChip.setOnClickListener { v ->
            val intent = Intent(this, PickContactActivity::class.java)
            startActivityForResult(intent, RQ_CODE_SECONDARY)
        }
        chip_group_sec_intro.addView(addChip)
    }

    private fun createAddContactChip(): Chip {
        val chip = Chip(this)
        chip.text = getString(R.string.add_contact)
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            this,
            R.color.chip_color_selector
        )
        chip.chipIcon = ContextCompat.getDrawable(this,
            R.drawable.ic_add
        )
        return chip
    }

    private fun getChipForContact(c: Contact): Chip {
        val chip = Chip(this)
        chip.text = if (c.name.isEmpty()) {
            c.number
        } else {
            c.name
        }
        chip.isCloseIconVisible = true
        chip.tag = c.id
        chip.setOnCloseIconClickListener { v ->
            contactsViewModel.deleteContactById(v!!.tag as Int)
        }
        return chip
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val number = data.getStringExtra("number")
            val name = data.getStringExtra("name")
            val isEmergency =
                if (requestCode == RQ_CODE_EMERGENCY) {
                    1
                } else {
                    0
                }
            val newContact = Contact(
                null,
                name = name,
                number = number,
                isEmergencyContact = isEmergency
            )
            contactsViewModel.addContact(newContact)
        }
    }
    
}