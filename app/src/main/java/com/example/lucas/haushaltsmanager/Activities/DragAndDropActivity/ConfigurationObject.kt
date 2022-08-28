package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import com.example.lucas.haushaltsmanager.entities.Price
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.github.mikephil.charting.data.Entry
import java.util.*
import kotlin.collections.ArrayList

/**
 * This object is used to transfer data between the configuration tabs e.g. TabOneDate and the widgets currently in the dropzone.
 */
class ConfigurationObject {
    private var bookings: ArrayList<Booking> = ArrayList()

    companion object {
        @JvmStatic
        fun createWithDefaults(): ConfigurationObject {
            val configuration = ConfigurationObject()
            configuration.addBookings(arrayListOf(
                Booking("Booking 1", Price(100.00), UUID.randomUUID(), UUID.randomUUID()),
                Booking("Booking 2", Price(50.00), UUID.randomUUID(), UUID.randomUUID()),
                Booking("Booking 3", Price(200.00), UUID.randomUUID(), UUID.randomUUID()),
                Booking("Booking 4", Price(300.00), UUID.randomUUID(), UUID.randomUUID()),
            ))

            return configuration
        }
    }

    fun addBookings(bookings: List<Booking>) {
        this.bookings.addAll(bookings)
    }

    fun getBookings(): List<Booking> {
        return this.bookings
    }
}