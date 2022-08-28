package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import com.example.lucas.haushaltsmanager.entities.booking.Booking

/**
 * This object is used to transfer data between the configuration tabs e.g. TabOneDate and the widgets currently in the dropzone.
 */
class ConfigurationObject {
    private var bookings: ArrayList<Booking> = ArrayList()

    fun addBookings(bookings: List<Booking>) {
        this.bookings.addAll(bookings)
    }

    fun getBookings(): List<Booking> {
        return this.bookings
    }
}