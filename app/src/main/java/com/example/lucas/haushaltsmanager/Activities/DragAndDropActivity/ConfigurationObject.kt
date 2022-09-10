package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.lucas.haushaltsmanager.entities.Price
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import java.util.*

/**
 * This object is used to transfer data between the configuration tabs e.g. TabOneDate and the widgets currently in the dropzone.
 */
class ConfigurationObject {
    private var query: SupportSQLiteQuery? = null
    private var bookings: ArrayList<Booking> = ArrayList()

    companion object {
        @JvmStatic
        fun createWithDefaults(): ConfigurationObject {
            val configuration = ConfigurationObject()
            configuration.addBookings(
                arrayListOf(
                    Booking("Booking 1", Price(100.00), UUID.randomUUID(), UUID.randomUUID()),
                    Booking("Booking 2", Price(50.00), UUID.randomUUID(), UUID.randomUUID()),
                    Booking("Booking 3", Price(200.00), UUID.randomUUID(), UUID.randomUUID()),
                    Booking("Booking 4", Price(300.00), UUID.randomUUID(), UUID.randomUUID()),
                )
            )

            return configuration
        }
    }

    fun addBookings(bookings: List<Booking>) {
        this.bookings.addAll(bookings)
    }

    fun addQuery(query: SupportSQLiteQuery) {
        this.query = query
    }

    fun getBookings(): List<Booking> {
        return this.bookings
    }

    fun getQuery(): SupportSQLiteQuery? {
        return this.query
    }
}