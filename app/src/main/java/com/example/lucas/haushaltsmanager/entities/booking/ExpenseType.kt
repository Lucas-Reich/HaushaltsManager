package com.example.lucas.haushaltsmanager.entities.booking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseType(val type: Boolean) : Parcelable {
    companion object {
        @JvmStatic
        fun deposit(): ExpenseType = ExpenseType(false)

        @JvmStatic
        fun expense(): ExpenseType = ExpenseType(true)

        @JvmStatic
        fun load(expenseType: Boolean) = ExpenseType(expenseType)
    }
}