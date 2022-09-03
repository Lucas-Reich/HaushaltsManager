package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import java.util.*

@Dao
interface BookingDAO {
    @Query("SELECT * FROM bookings")
    fun getAll(): List<Booking>

    @RawQuery
    fun getFilteredList(query: SupportSQLiteQuery): List<Booking>

    @Query("SELECT * FROM bookings WHERE account_id IN (:accountIds)")
    fun getAllWithAccounts(accountIds: List<UUID>): List<Booking>

    @Insert
    fun insert(booking: Booking)

    @Update
    fun update(booking: Booking)

    @Delete
    fun delete(booking: Booking)
}