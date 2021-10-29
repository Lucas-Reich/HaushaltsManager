package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.IBooking

@Dao
interface BookingDAO {
    @Query("SELECT * FROM bookings")
    fun getAll(): List<IBooking>

    @Insert
    fun insert(booking: Booking) // TODO: Insert children if booking has some

    @Update
    fun update(booking: Booking)

    @Delete
    fun delete(booking: Booking) // TODO: Do not delete if booking has children attached
}