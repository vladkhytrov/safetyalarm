package com.aegis.petasos.fragment.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aegis.petasos.App
import com.aegis.petasos.R
import com.aegis.petasos.activity.PickContactActivity
import com.aegis.petasos.data.db.Contact
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_intro_contacts.*

class ContactsFragment : Fragment(R.layout.fragment_intro_contacts) {

    private val RQ_CODE_EMERGENCY = 1
    private val RQ_CODE_SECONDARY = 2

    private lateinit var contactsViewModel: ContactsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsViewModel = ViewModelProvider(
            this,
            ContactsViewModel.Factory(
                (requireActivity().application as App).db
            )
        ).get(ContactsViewModel::class.java)

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
            val intent = Intent(requireContext(), PickContactActivity::class.java)
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
            val intent = Intent(requireContext(), PickContactActivity::class.java)
            startActivityForResult(intent, RQ_CODE_SECONDARY)
        }
        chip_group_sec_intro.addView(addChip)
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