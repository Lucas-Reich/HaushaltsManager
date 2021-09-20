package com.example.lucas.haushaltsmanager.entities

import com.example.lucas.haushaltsmanager.entities.Booking.IBooking
import java.util.*
import java.util.concurrent.TimeUnit

class RecurringBookingKt(
    val id: UUID,
    val end: Calendar,
    val frequency: Frequency,
    val templateBooking: IBooking
) {
    private var executionDate: Calendar = Calendar.getInstance()
        set(executionDate) {
            field = executionDate
            templateBooking.date = executionDate
        }

    constructor(
        start: Calendar,
        end: Calendar,
        frequency: Frequency,
        templateBooking: IBooking
    ) : this(UUID.randomUUID(), start, end, frequency, templateBooking)

    companion object {
        fun createNextRecurringBooking(recurringBooking: RecurringBookingKt): RecurringBookingKt? {
            val start = recurringBooking.getNextOccurrence()
            if (start.after(recurringBooking.end)) {
                return null
            }

            return RecurringBookingKt(
                start,
                recurringBooking.end,
                recurringBooking.frequency,
                recurringBooking.templateBooking
            )
        }
    }

    fun getDelayUntilNextExecution(): Delay {
        var timeBetween = getTimeBetweenNowAnd(executionDate)
        if (timeBetween < 0) {
            timeBetween = getTimeBetweenNowAnd(getNextOccurrence())
        }

        return Delay(
            TimeUnit.MILLISECONDS,
            timeBetween
        )
    }

    private fun getNextOccurrence(): Calendar {
        val next = Calendar.getInstance()

        next.timeInMillis = executionDate.timeInMillis
        next.add(frequency.calendarField, frequency.amount)

        return next
    }

    private fun getTimeBetweenNowAnd(otherDate: Calendar): Long {
        val now = Calendar.getInstance()

        return otherDate.timeInMillis - now.timeInMillis
    }
}