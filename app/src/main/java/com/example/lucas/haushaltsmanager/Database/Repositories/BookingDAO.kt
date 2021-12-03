package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.booking.Booking

@Dao
interface BookingDAO {
    @Query("SELECT * FROM bookings")
    fun getAll(): List<Booking>

    @Insert
    fun insert(booking: Booking)

    @Update
    fun update(booking: Booking)

    @Delete
    fun delete(booking: Booking) // TODO: Do not delete if booking has children attached
}