package com.example.lucas.haushaltsmanager.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.util.concurrent.TimeUnit

@Entity(tableName = "recurring_bookings")
class RecurringBooking(
    @PrimaryKey val id: UUID,
    val date: Calendar,
    @ColumnInfo(name = "end_date") val endDate: Calendar,
    val frequency: Frequency,
    val title: String,
    val price: Price,
    @ColumnInfo(name = "category_id") val categoryId: UUID,
    @ColumnInfo(name = "account_id") val accountId: UUID
) {
    constructor(
        date: Calendar,
        endDate: Calendar,
        frequency: Frequency,
        title: String,
        price: Price,
        categoryId: UUID,
        accountId: UUID
    ) : this(UUID.randomUUID(), date, endDate, frequency, title, price, categoryId, accountId)

    companion object {
        @JvmStatic
        fun createNextRecurringBooking(recurringBooking: RecurringBooking): RecurringBooking? {
            val start = recurringBooking.getNextOccurrence();
            if (start.after(recurringBooking.endDate)) {
                return null
            }

            return RecurringBooking(
                start,
                recurringBooking.endDate,
                recurringBooking.frequency,
                recurringBooking.title,
                recurringBooking.price,
                recurringBooking.categoryId,
                recurringBooking.accountId
            )
        }
    }

    fun getDelayUntilNextExecution(): Delay {
        var timeBetween = getTimeBetweenNowAnd(date)
        if (timeBetween < 0) {
            timeBetween = getTimeBetweenNowAnd(getNextOccurrence())
        }

        return Delay(
            TimeUnit.MILLISECONDS,
            timeBetween
        )
    }

    private fun getNextOccurrence(): Calendar {
        val nextOccurrence = date.clone() as Calendar

        nextOccurrence.add(frequency.calendarField, frequency.amount)

        return nextOccurrence
    }

    private fun getTimeBetweenNowAnd(otherDate: Calendar): Long {
        val now = Calendar.getInstance()

        return otherDate.timeInMillis - now.timeInMillis
    }
}