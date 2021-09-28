package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingWithoutCategory

@Dao
interface TemplateBookingDAO {
    @Insert
    fun insert(templateBooking: TemplateBookingWithoutCategory)

    @Query("SELECT * FROM template_bookings")
    fun getTemplatesAndCategories(): List<TemplateBooking>
}