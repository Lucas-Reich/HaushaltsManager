package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.RecurringBooking
import java.util.*

@Dao
interface RecurringBookingDAO {
    @Insert
    fun insert(recurringBooking: RecurringBooking)

    @Query("SELECT * FROM recurring_bookings WHERE id = :id")
    fun get(id: UUID): RecurringBooking

    @Query("SELECT * FROM recurring_bookings WHERE date BETWEEN :start and :end")
    fun getAll(start: Calendar, end: Calendar): List<RecurringBooking>

    @Update
    fun update(recurringBooking: RecurringBooking)

    @Delete
    fun delete(recurringBooking: RecurringBooking)
}