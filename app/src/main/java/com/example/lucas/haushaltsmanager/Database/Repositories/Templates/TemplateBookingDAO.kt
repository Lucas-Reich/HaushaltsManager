package com.example.lucas.haushaltsmanager.Database.Repositories.Templates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lucas.haushaltsmanager.entities.TemplateBooking

@Dao
interface TemplateBookingDAO {
    @Query("SELECT * FROM template_bookings")
    fun getAll(): List<TemplateBooking>

    @Insert
    fun insert(templateBooking: TemplateBooking);
}