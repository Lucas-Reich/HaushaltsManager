package com.example.lucas.haushaltsmanager.entities.booking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseType(val type: Boolean) : Parcelable {
    companion object {
        fun deposit(): ExpenseType = ExpenseType(false)

        fun expense(): ExpenseType = ExpenseType(true)

        fun load(expenseType: Boolean) = ExpenseType(expenseType)
    }
}