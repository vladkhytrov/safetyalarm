package com.aegis.petasos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pick_contact.*

class PickContactActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_contact)

        btn_back_pc.setOnClickListener {
            onBackPressed()
        }

        btn_choose_phone_contact.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, 1)
        }

        btn_confirm_contact.setOnClickListener {
            val number = et_number_pick.text.toString().trim()
            if (number.isEmpty()) {
                Toast.makeText(this, "Phone number is empty", Toast.LENGTH_SHORT).show()
            } else {
                val name = et_name_pick.text.toString().trim()
                val data = Intent().apply {
                    putExtra("number", number)
                    putExtra("name", name)
                }
                setResult(RESULT_OK, data)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            try {
                val uri = data.data
                val cursor = contentResolver.query(uri!!, null, null, null, null)
                cursor!!.moveToFirst()
                val phoneIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNo = cursor.getString(phoneIndex)
                val name = cursor.getString(nameIndex)

                et_name_pick.setText(name)
                et_number_pick.setText(phoneNo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}