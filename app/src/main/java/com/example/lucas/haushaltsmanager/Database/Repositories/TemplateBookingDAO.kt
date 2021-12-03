package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingWithoutCategory

@Dao
interface TemplateBookingDAO {
    @Insert
    fun insert(templateBooking: TemplateBookingWithoutCategory)

    @Transaction
    @Query("SELECT * FROM template_bookings")
    fun getTemplatesAndCategories(): List<TemplateBooking>
}