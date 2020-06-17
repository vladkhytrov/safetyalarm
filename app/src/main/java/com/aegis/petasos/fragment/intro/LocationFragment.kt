package com.aegis.petasos.fragment.intro

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aegis.petasos.R
import com.aegis.petasos.data.UserStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_intro_location.*

class LocationFragment : Fragment(R.layout.fragment_intro_location) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userStorage = UserStorage(requireContext())
        location_switch_intro.isChecked = userStorage.isLocationEnabled()

        location_switch_intro.setOnCheckedChangeListener { buttonView, isChecked ->
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
                                userStorage.setLocationEnabled(true)
                            } else {
                                userStorage.setLocationEnabled(false)
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
                userStorage.setLocationEnabled(false)
            }
        }
    }
}