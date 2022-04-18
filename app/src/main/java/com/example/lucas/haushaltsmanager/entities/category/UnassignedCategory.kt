package com.example.lucas.haushaltsmanager.entities.category

import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.entities.Color
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType

class UnassignedCategory : Category(
    app.notAssignedCategoryId,
    app.getStringResource(R.string.category_unassigned),
    Color("#f5d742"),
    ExpenseType.expense()
)