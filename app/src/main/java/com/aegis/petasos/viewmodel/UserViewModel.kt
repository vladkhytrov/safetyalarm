package com.aegis.petasos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aegis.petasos.data.UserStorage

class UserViewModel(private val userStorage: UserStorage) : ViewModel() {

    class Factory(private val userStorage: UserStorage) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserViewModel(userStorage) as T
        }

    }

    val username = MutableLiveData<String>()
    val locationEnabled = MutableLiveData<Boolean>()

    init {
        refresh()
    }

    private fun refresh() {
        username.value = userStorage.getName()
        locationEnabled.value = userStorage.isLocationEnabled()
    }

    fun setUsername(name: String) {
        userStorage.setName(name)
        username.value = userStorage.getName()
        refresh()
    }

    fun setLocationEnabled(enabled: Boolean) {
        userStorage.setLocationEnabled(enabled)
        refresh()
    }

}