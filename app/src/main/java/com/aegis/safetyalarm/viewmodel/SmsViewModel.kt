package com.aegis.safetyalarm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aegis.safetyalarm.data.SmsStorage

class SmsViewModel(private val smsStorage: SmsStorage) : ViewModel() {

    class Factory(private val smsStorage: SmsStorage) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SmsViewModel(smsStorage) as T
        }

    }

    val workId = MutableLiveData<String>()
    val time = MutableLiveData<Long>()
    val msg = MutableLiveData<String>()

    init {
        refresh()
    }

    private fun refresh() {
        workId.value = smsStorage.getWorkId()
        time.value = smsStorage.getTime()
        msg.value = smsStorage.getMsg()
    }

    fun setWork(workId: String, time: Long, msg: String) {
        smsStorage.saveWork(workId)
        smsStorage.saveTime(time)
        smsStorage.saveMsg(msg)
        refresh()
    }

    fun setTime(time: Long) {
        smsStorage.saveTime(time)
        this.time.value = time
    }

}