package com.aegis.petasos

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.aegis.petasos.data.SmsStorage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordDialog {

    /*interface Callback {
        fun onResult(success: Boolean)
    }

    companion object {

        fun showPassLock(ctx: Context, callback: Callback) {
            val smsStorage = SmsStorage(ctx)
            val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_pass_lock, null)
            val etPass = view.findViewById<TextInputEditText>(R.id.et_pass)

            AlertDialog.Builder(ctx)
                .setView(view)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    val passInput = etPass.text.toString().trim()
                    //smsStorage.savePass(passInput)
                    callback.onResult(true)
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        fun showPassUnlock(ctx: Context, callback: Callback) {
            val smsStorage = SmsStorage(ctx)
            val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_pass_unlock, null)
            val etPass = view.findViewById<TextInputEditText>(R.id.et_pass)
            val layoutPass = view.findViewById<TextInputLayout>(R.id.et_pass_layout)
            val btnCancel = view.findViewById<MaterialButton>(R.id.btn_unlock_cancel)
            val btnOk = view.findViewById<MaterialButton>(R.id.btn_unlock_ok)
            etPass.doAfterTextChanged {
                layoutPass.error = null
            }

            val dialog = AlertDialog.Builder(ctx)
                .setView(view)
                .create()
                dialog.show()
            btnCancel.setOnClickListener {
                callback.onResult(false)
                dialog.dismiss()
            }
            btnOk.setOnClickListener {
                /*val passInput = etPass.text.toString().trim()
                val pass = smsStorage.getPass()
                if (pass == passInput) {
                    callback.onResult(true)
                    dialog.dismiss()
                } else {
                    layoutPass.error = ctx.getString(R.string.validation_password)
                }*/
            }
        }

    }*/

}