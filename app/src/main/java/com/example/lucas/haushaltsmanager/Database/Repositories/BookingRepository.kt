package com.example.lucas.haushaltsmanager.Database.Repositories

import com.example.lucas.haushaltsmanager.entities.booking.IBooking
import java.util.*

class BookingRepository(
    private val bookingDAO: BookingDAO,
    private val parentBookingDAO: ParentBookingDAO
) {
    fun getBookingsWithAccount(accounts: List<UUID>): List<IBooking> {
        val bookings = bookingDAO.getAllWithAccounts(accounts)

        val parentBookings = parentBookingDAO.getAllWithAccounts(accounts)
        for ((parent, children) in parentBookings) {
            parent.addChildren(children)
        }

        val mergedList = ArrayList<IBooking>()
        mergedList.addAll(bookings)
        mergedList.addAll(parentBookings.keys)
        return mergedList
    }
}