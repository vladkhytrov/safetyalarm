package com.aegis.petasos.fragment.intro

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.aegis.petasos.R
import com.aegis.petasos.data.UserStorage
import kotlinx.android.synthetic.main.fragment_intro_name.*

class NameFragment : Fragment(R.layout.fragment_intro_name) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_username_intro.doAfterTextChanged {
            username_layout.error = null
        }
    }

    fun validateName(): Boolean {
        val username = et_username_intro.text.toString().trim()
        if (username.isEmpty()) {
            username_layout.error = getString(R.string.validation_name)
            return false
        } else {
            UserStorage(requireContext()).setName(username)
            return true
        }
    }

}