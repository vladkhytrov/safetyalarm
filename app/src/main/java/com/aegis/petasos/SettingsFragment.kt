package com.aegis.petasos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import com.google.android.material.chip.Chip
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

        userViewModel.username.observe(viewLifecycleOwner, Observer {
            et_username.setText(it)
        })

        contactsViewModel.emergencyContacts.observe(viewLifecycleOwner, Observer {
            renderEmContacts(it)
        })

        contactsViewModel.secondaryContacts.observe(viewLifecycleOwner, Observer {
            renderSecContacts(it)
        })
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
        chip.text = "Add contact"
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            R.color.chip_color_selector
        )
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)
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