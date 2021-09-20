package com.example.lucas.haushaltsmanager.entities

import androidx.annotation.StringRes
import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum
import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType.Companion.deposit
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking
import com.example.lucas.haushaltsmanager.entities.Booking.ParentBooking
import com.example.lucas.haushaltsmanager.entities.Color.Companion.white
import java.util.*

class Report(var title: String, val bookings: MutableList<IBooking>) {
    fun getTotal(): Double {
        return getIncoming() + getOutgoing()
    }

    fun getIncoming(): Double {
        val expenseSum = ExpenseSum()

        return expenseSum.byExpenditureTypeNew(false, bookings)
    }

    fun getOutgoing(): Double {
        val expenseSum = ExpenseSum()

        return expenseSum.byExpenditureTypeNew(true, bookings)
    }

    fun getBookingCount(): Int {
        return getBookingCount(bookings)
    }

    fun getMostStressedCategory(): Category {
        val categories = ExpenseSum().byCategoryNew(bookings)

        return getMaxEntry(categories)
    }

    private fun getResourceString(@StringRes stringRes: Int): String {
        return app.getContext().getString(stringRes)
    }

    private fun getPlaceholderCategory(@StringRes titleRes: Int): Category {
        return Category(
            getResourceString(titleRes),
            white(),
            deposit()
        )
    }

    private fun getMaxEntry(expenseSumByCategory: HashMap<Category, Double>): Category {
        if (expenseSumByCategory.isEmpty()) {
            return getPlaceholderCategory(R.string.no_expenses)
        }

        var minCategory: Map.Entry<Category, Double> = expenseSumByCategory.entries.first()
        for (entry in expenseSumByCategory.entries) {
            if (entry.value < minCategory.value) {
                minCategory = entry
            }
        }

        return minCategory.key
    }

    private fun getBookingCount(bookings: List<IBooking>): Int {
        var count = 0

        for (booking in bookings) {
            if (booking !is ParentBooking) {
                count += 1
                continue
            }

            count += booking.children.size
        }

        return count
    }
}