package com.aegis.petasos

import android.app.Application
import androidx.room.Room
import com.aegis.petasos.data.UserStorage
import com.aegis.petasos.data.db.AppDB
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore

class App : Application() {

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDB::class.java, "aegis-db"
        )
            .allowMainThreadQueries()
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // the app supports 3 locales: english, chinese simplified and traditional chinese.
        //val userStore = UserStorage(this)
        //val locale = userStore.getLang().getLocale()
        val store = PreferenceLocaleStore(this)
        //store.persistLocale(locale)
        val lingver = Lingver.init(this, store)
    }

}