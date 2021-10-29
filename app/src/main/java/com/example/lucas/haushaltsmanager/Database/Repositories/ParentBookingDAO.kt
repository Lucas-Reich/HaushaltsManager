package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking

@Dao
interface ParentBookingDAO {
    @Query("SELECT * FROM parent_bookings JOIN bookings on parent_bookings.id = bookings.parent_id")
    fun getAll(): Map<ParentBooking, List<Booking>>

    // TODO: Can those two methods be combined?
    fun insert(parentBooking: ParentBooking)
    fun insert(
        parentBooking: ParentBooking,
        childBooking: Booking
    ) // TODO: Insert parent if not existing

    @Insert
    fun insert(parentBooking: ParentBooking, children: List<Booking>)

    fun delete(parentBooking: ParentBooking) // TODO: Parents cannot be deleted, only children of a parent. To delete a parent remove all children.
    fun delete(childBooking: Booking) // TODO: Delete parent if child is last of parent
    fun extractChildFromParent() // TODO: Set parent id to NULL and check if parent needs to be deleted
}