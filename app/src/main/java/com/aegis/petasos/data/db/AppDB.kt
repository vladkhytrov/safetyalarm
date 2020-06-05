package com.aegis.petasos.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Contact::class), version = 1)
abstract class AppDB : RoomDatabase() {

    abstract fun contactDao(): ContactDao

}