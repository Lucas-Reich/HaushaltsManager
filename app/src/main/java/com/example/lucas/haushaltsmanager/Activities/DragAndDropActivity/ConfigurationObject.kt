package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils
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
                    Booking(UUID.randomUUID(), "Booking 1", Price(1000.00), CalendarUtils.fromString("01.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 2", Price(-350.00), CalendarUtils.fromString("04.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 3", Price(-50.00), CalendarUtils.fromString("07.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 4", Price(-225.00), CalendarUtils.fromString("15.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 5", Price(-75.00), CalendarUtils.fromString("16.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 6", Price(-415.00), CalendarUtils.fromString("17.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 7", Price(-85.00), CalendarUtils.fromString("25.01.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 8", Price(1000.00), CalendarUtils.fromString("01.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 9", Price(-100.00), CalendarUtils.fromString("04.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 10", Price(-50.00), CalendarUtils.fromString("10.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 11", Price(-50.00), CalendarUtils.fromString("11.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 12", Price(-250.00), CalendarUtils.fromString("18.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 13", Price(-25.00), CalendarUtils.fromString("20.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 14", Price(-25.00), CalendarUtils.fromString("23.02.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 15", Price(1000.00), CalendarUtils.fromString("01.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 16", Price(-300.00), CalendarUtils.fromString("04.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 17", Price(-100.00), CalendarUtils.fromString("05.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 18", Price(-100.00), CalendarUtils.fromString("10.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 19", Price(-100.00), CalendarUtils.fromString("12.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 20", Price(-100.00), CalendarUtils.fromString("20.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 21", Price(-100.00), CalendarUtils.fromString("24.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 22", Price(70.00), CalendarUtils.fromString("24.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 23", Price(-300.00), CalendarUtils.fromString("24.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
                    Booking(UUID.randomUUID(), "Booking 24", Price(-120.00), CalendarUtils.fromString("24.03.2022", "dd.MM.yyyy"), UUID.randomUUID(), UUID.randomUUID()),
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