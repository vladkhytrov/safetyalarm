package com.aegis.safetyalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aegis.safetyalarm.data.ContactStorage
import com.aegis.safetyalarm.data.UserStorage
import kotlinx.android.synthetic.main.activity_settings.*

// todo validation
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btn_back.setOnClickListener {
            onBackPressed()
        }

        val userStorage = UserStorage(this)
        val contactStorage = ContactStorage(this)

        val name = userStorage.getName()
        val number = contactStorage.getContact()
        et_name.setText(name)
        et_contact.setText(number)

        btn_save.setOnClickListener {
            val newName = et_name.text.toString().trim()
            val newContact = et_contact.text.toString().trim()
            userStorage.setName(newName)
            contactStorage.setContact(newContact)
        }

        btn_add_contact.setOnClickListener {
            val intent = Intent(this, PickContactActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val number = data.getStringExtra("number")
            val name = data.getStringExtra("name")
            et_name.setText(name)
            et_contact.setText(number)

            val userStorage = UserStorage(this)
            val contactStorage = ContactStorage(this)
            userStorage.setName(name)
            contactStorage.setContact(number)
        }
    }

}