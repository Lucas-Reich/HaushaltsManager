package com.example.lucas.haushaltsmanager.entities.category

import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.entities.Color
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType

class TransferCategory: Category(
    app.transferCategoryId,
    app.getStringResource(R.string.category_transfer),
    Color("#a442f5"),
    ExpenseType.expense()
) {
}