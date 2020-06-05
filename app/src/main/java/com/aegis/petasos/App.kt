package com.aegis.petasos

import android.app.Application
import androidx.room.Room
import com.aegis.petasos.data.db.AppDB

class App : Application() {

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDB::class.java, "aegis-db"
        )
            .allowMainThreadQueries()
            .build()
    }

}