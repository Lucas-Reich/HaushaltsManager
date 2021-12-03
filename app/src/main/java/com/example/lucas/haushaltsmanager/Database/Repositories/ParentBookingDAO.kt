package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking

@Dao
abstract class ParentBookingDAO {

    @Query("SELECT * FROM parent_bookings JOIN bookings on parent_bookings.id = bookings.parent_id")
    abstract fun getAll(): Map<ParentBooking, List<Booking>>

    @Insert
    fun insert(parentBooking: ParentBooking) {
        insert(parentBooking, parentBooking.children)
    }

    @Insert(onConflict = REPLACE)
    abstract fun insert(parentBooking: ParentBooking, children: List<Booking>)

    fun extractChildFromParent(childExpense: Booking): Booking {
        childExpense.parentId = null // TODO: Check if parent needs to be deleted
        updateChildBooking(childExpense)

        return childExpense
    }

    @Update
    abstract fun updateChildBooking(childBooking: Booking);

    fun deleteChildBooking(childBooking: Booking) {
        // TODO: Delete parent if child was the last one

        delete(childBooking)
    }

    @Delete
    abstract fun delete(childBooking: Booking)
}