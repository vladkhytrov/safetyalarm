package com.aegis.safetyalarm.data.db

import androidx.room.*
import com.aegis.safetyalarm.data.db.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact WHERE is_emergency_contact = 1")
    fun getEmergency(): List<Contact>

    @Query("SELECT * FROM contact WHERE is_emergency_contact = 0")
    fun getSecondary(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(vararg contacts: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("DELETE FROM contact WHERE id = :id")
    fun deleteById(id: Int)
}