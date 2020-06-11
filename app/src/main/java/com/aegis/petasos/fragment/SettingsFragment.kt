package com.aegis.petasos.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aegis.petasos.R
import com.aegis.petasos.activity.PickContactActivity
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val RQ_CODE_EMERGENCY = 1
    private val RQ_CODE_SECONDARY = 2

    private val contactsViewModel by activityViewModels<ContactsViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        btn_battery_info.setOnClickListener {
            showBatteryDialog()
        }

        location_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                Dexter.withContext(requireContext())
                    .withPermissions(permissions)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(result: MultiplePermissionsReport) {
                            if (result.areAllPermissionsGranted()) {
                                userViewModel.setLocationEnabled(true)
                            } else {
                                userViewModel.setLocationEnabled(false)
                                location_switch.isChecked = false
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

        userViewModel.username.observe(viewLifecycleOwner, Observer {
            et_username.setText(it)
        })

        userViewModel.locationEnabled.observe(viewLifecycleOwner, Observer {
            location_switch.isChecked = it
        })

        contactsViewModel.emergencyContacts.observe(viewLifecycleOwner, Observer {
            renderEmContacts(it)
        })

        contactsViewModel.secondaryContacts.observe(viewLifecycleOwner, Observer {
            renderSecContacts(it)
        })
    }

    private fun showBatteryDialog() {
        val batteryView = layoutInflater.inflate(R.layout.dialog_battery_saver, null)
        val close = batteryView.findViewById<MaterialButton>(R.id.btn_battery_close)
        val settings = batteryView.findViewById<MaterialButton>(R.id.btn_battery_settings)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(batteryView)
            .create()
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
        settings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            dialog.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        userViewModel.setUsername(et_username.text.toString().trim())
    }

    private fun renderEmContacts(contacts: List<Contact>) {
        chip_group_em.removeAllViews()
        contacts.forEach {
            chip_group_em.addView(getChipForContact(it))
        }
        val addChip = createAddContactChip()
        addChip.setOnClickListener { v ->
            val intent = Intent(requireContext(), PickContactActivity::class.java)
            startActivityForResult(intent, RQ_CODE_EMERGENCY)
        }
        chip_group_em.addView(addChip)
    }

    private fun renderSecContacts(contacts: List<Contact>) {
        chip_group_sec.removeAllViews()
        contacts.forEach {
            chip_group_sec.addView(getChipForContact(it))
        }
        val addChip = createAddContactChip()
        addChip.setOnClickListener { v ->
            val intent = Intent(requireContext(), PickContactActivity::class.java)
            startActivityForResult(intent, RQ_CODE_SECONDARY)
        }
        chip_group_sec.addView(addChip)
    }

    private fun createAddContactChip(): Chip {
        val chip = Chip(requireContext())
        chip.text = getString(R.string.add_contact)
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            R.color.chip_color_selector
        )
        chip.chipIcon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_add
        )
        return chip
    }

    private fun getChipForContact(c: Contact): Chip {
        val chip = Chip(requireContext())
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