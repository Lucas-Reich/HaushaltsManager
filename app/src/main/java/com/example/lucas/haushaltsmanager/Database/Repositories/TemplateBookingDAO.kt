package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingAndCategory

@Dao
interface TemplateBookingDAO {
    @Query("SELECT * FROM template_bookings")
    fun getAll(): List<TemplateBooking>

    @Query("SELECT * FROM template_bookings")
    fun getTemplatesAndCategories(): List<TemplateBookingAndCategory>

    @Insert
    fun insert(templateBooking: TemplateBooking);
}