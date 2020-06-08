package com.aegis.petasos.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.aegis.petasos.R
import com.aegis.petasos.activity.MainActivity
import com.aegis.petasos.viewmodel.ContactsViewModel
import com.aegis.petasos.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_settings_widget.*

class SettingsWidgetFragment : Fragment(R.layout.fragment_settings_widget) {

    private val contactsViewModel by activityViewModels<ContactsViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_settings.setOnClickListener {
            (activity as MainActivity).openSettings()
        }

        userViewModel.locationEnabled.observe(viewLifecycleOwner, Observer {
            if (it) {
                sett_location.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.location_on)
                )
            } else {
                sett_location.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.location_off)
                )
            }
        })
        userViewModel.username.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                sett_name.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.name_on)
                )
            } else {
                sett_name.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.name_off)
                )
            }
        })
        contactsViewModel.emergencyContacts.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                sett_em_contact.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.contact_on)
                )
            } else {
                sett_em_contact.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.contact_off)
                )
            }
        })
        contactsViewModel.secondaryContacts.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                sett_sec_contact.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.secondary_on)
                )
            } else {
                sett_sec_contact.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.secondary_off)
                )
            }
        })
    }

}