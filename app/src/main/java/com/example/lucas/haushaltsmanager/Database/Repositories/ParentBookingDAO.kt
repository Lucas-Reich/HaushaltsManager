package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking
import java.util.*

@Dao
interface ParentBookingDAO {
    @Query("SELECT * FROM parent_bookings JOIN bookings on parent_bookings.id = bookings.parent_id")
    fun getAll(): Map<ParentBooking, List<Booking>>

    @Query("SELECT * FROM parent_bookings JOIN bookings on parent_bookings.id = bookings.parent_id WHERE bookings.id IN (:accountIds)")
    fun getAllWithAccounts(accountIds: List<UUID>): Map<ParentBooking, List<Booking>>

    @Query("DELETE FROM parent_bookings WHERE id = :parentId AND :parentId NOT IN (SELECT parent_id FROM bookings)")
    fun deleteParentWhenNotReferenced(parentId: UUID)

    @Insert(onConflict = REPLACE)
    fun insert(parentBooking: ParentBooking, children: List<Booking>)
}