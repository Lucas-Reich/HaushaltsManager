package com.example.lucas.haushaltsmanager.entities.template_booking

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Price
import com.example.lucas.haushaltsmanager.entities.UUIDParceler
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.util.*

@Parcelize
@Entity(tableName = "template_bookings")
class TemplateBookingWithoutCategory(
    @PrimaryKey val id: @WriteWith<UUIDParceler> UUID,
    val title: String,
    val price: Price,
    val date: Calendar,
    @ColumnInfo(name = "category_id") val categoryId: UUID,
    @ColumnInfo(name = "account_id") val accountId: UUID
) : Parcelable {
    constructor(template: Booking) : this(
        UUID.randomUUID(),
        template.title,
        template.price,
        template.date,
        template.category.id,
        template.accountId
    )
}