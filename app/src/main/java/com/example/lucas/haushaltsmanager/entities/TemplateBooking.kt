package com.example.lucas.haushaltsmanager.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Booking.Booking
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.util.*

@Parcelize
@Entity(tableName = "template_bookings")
class TemplateBooking(
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