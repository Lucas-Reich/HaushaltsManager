package com.example.lucas.haushaltsmanager.entities

import android.os.Parcelable
import com.example.lucas.haushaltsmanager.entities.Booking.Booking
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class TemplateBooking(val id: UUID, val template: Booking) : Parcelable {

    constructor(template: Booking) : this(UUID.randomUUID(), template)

    fun getTitle(): String {
        return template.title
    }

    fun getCategory(): Category {
        return template.category
    }

    fun getPrice(): Price {
        return template.price
    }
}