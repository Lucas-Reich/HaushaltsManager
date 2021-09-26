package com.example.lucas.haushaltsmanager.entities

import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class TemplateBookingAndCategory(
    @Embedded val templateBooking: TemplateBooking,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category,
) {
    fun getId(): UUID {
        return templateBooking.id
    }
}