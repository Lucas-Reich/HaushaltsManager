package com.example.lucas.haushaltsmanager.Database.Repositories

import com.example.lucas.haushaltsmanager.entities.booking.Booking

class ParentBookingRepository(
    private val parentBookingDAO: ParentBookingDAO,
    private val bookingDAO: BookingDAO
) {
    fun extractChildFromParent(childExpense: Booking): Booking {
        val parentId = childExpense.parentId
        childExpense.parentId = null

        bookingDAO.update(childExpense)
        parentBookingDAO.deleteParentWhenNotReferenced(parentId!!)

        return childExpense
    }

    fun deleteChildBooking(childBooking: Booking) {
        val parentId = childBooking.parentId

        bookingDAO.delete(childBooking)
        parentBookingDAO.deleteParentWhenNotReferenced(parentId!!)
    }
}