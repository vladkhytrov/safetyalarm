package com.aegis.safetyalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aegis.safetyalarm.R
import com.aegis.safetyalarm.data.ContactStorage
import com.aegis.safetyalarm.data.UserStorage
import kotlinx.android.synthetic.main.activity_settings.*

// todo validation
// todo phone contacts provider
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val userStorage = UserStorage(this)
        val contactStorage = ContactStorage(this)

        val name = userStorage.getName()
        val contact = contactStorage.getContact()
        et_name.setText(name)
        et_contact.setText(contact)

        btn_save.setOnClickListener {
            val newName = et_name.text.toString().trim()
            val newContact = et_contact.text.toString().trim()
            userStorage.setName(newName)
            contactStorage.setContact(newContact)
        }
    }

}