package com.example.lucas.haushaltsmanager.entities.template_booking

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lucas.haushaltsmanager.entities.Category
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