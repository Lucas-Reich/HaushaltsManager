package com.example.lucas.haushaltsmanager

import com.example.lucas.haushaltsmanager.entities.Price
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking
import java.util.*

class TestUtil {
    fun createParentBooking(id: UUID): ParentBooking {
        return ParentBooking(
            id,
            Calendar.getInstance(),
            "parent",
            ArrayList()
        )
    }

    fun createParentBookingWithChildren(id: UUID, children: Int): ParentBooking {
        return ParentBooking(
            id,
            Calendar.getInstance(),
            "parent",
            createListOfBookings(children)
        )
    }

    fun createListOfBookings(amount: Int): ArrayList<Booking> {
        val bookings = ArrayList<Booking>()

        for (i in 1..amount) {
            bookings.add(createBooking())
        }

        return bookings
    }

    fun createBooking(): Booking {
        return Booking(
            "Booking",
            Price(0.0),
            UUID.randomUUID(),
            UUID.randomUUID()
        )
    }
}