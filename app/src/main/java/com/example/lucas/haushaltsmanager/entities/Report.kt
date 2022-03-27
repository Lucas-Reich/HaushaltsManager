package com.example.lucas.haushaltsmanager.entities

import androidx.annotation.StringRes
import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum
import com.example.lucas.haushaltsmanager.entities.Color.Companion.white
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType.Companion.deposit
import java.util.*

class Report(var title: String, val bookings: MutableList<Booking>) {
    fun getTotal(): Double {
        return getIncoming() + getOutgoing()
    }

    fun getIncoming(): Double {
        val expenseSum = ExpenseSum()

        return expenseSum.byExpenditureType(false, bookings)
    }

    fun getOutgoing(): Double {
        val expenseSum = ExpenseSum()

        return expenseSum.byExpenditureType(true, bookings)
    }

    fun getBookingCount(): Int {
        return bookings.size
    }

    fun getMostStressedCategory(): Category {
        val categories = ExpenseSum().byCategory(bookings)

        return getMaxEntry(categories)
    }

    private fun getResourceString(@StringRes stringRes: Int): String {
        return app.getStringResource(stringRes)
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
}