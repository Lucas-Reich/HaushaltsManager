package com.example.lucas.haushaltsmanager.entities.template_booking

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.example.lucas.haushaltsmanager.entities.Category
import com.example.lucas.haushaltsmanager.entities.Price
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TemplateBooking(
    @Embedded val templateBooking: TemplateBookingWithoutCategory,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category,
) : Parcelable {
    fun getId(): UUID {
        return templateBooking.id
    }

    fun getTitle(): String {
        return templateBooking.title
    }

    fun getPrice(): Price {
        return templateBooking.price
    }

    fun getDate(): Calendar {
        return templateBooking.date
    }

    fun getAccountId(): UUID {
        return templateBooking.accountId
    }
}