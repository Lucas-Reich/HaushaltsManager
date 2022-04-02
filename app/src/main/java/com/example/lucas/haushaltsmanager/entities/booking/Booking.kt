package com.example.lucas.haushaltsmanager.entities.booking

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Price
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "bookings")
@Parcelize
class Booking(
    @PrimaryKey val id: UUID,
    var title: String,
    var price: Price,
    private var date: Calendar,
    @ColumnInfo(name = "category_id") var categoryId: UUID,
    @ColumnInfo(name = "account_id") var accountId: UUID,
    @ColumnInfo(name = "parent_id") var parentId: UUID? = null
) : Parcelable, IBooking {
    constructor(
        title: String,
        price: Price,
        categoryId: UUID,
        accountId: UUID
    ) : this(UUID.randomUUID(), title, price, Calendar.getInstance(), categoryId, accountId)

    override fun getDate(): Calendar {
        return date
    }

    fun setDate(date: Calendar) {
        this.date = date
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Booking) {
            return false
        }

        return title == other.title
                && price == other.price
                && accountId == other.accountId
                && date == other.date
                && categoryId == other.categoryId
    }

    fun getDisplayableDateTime(): String {
        return DateFormat.getDateInstance(DateFormat.SHORT)
            .format(Date(date.timeInMillis))
    }

    fun getDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.time)
    }

    fun getUnsignedPrice(): Double {
        return price.absoluteValue
    }

    fun isExpenditure(): Boolean {
        return price.isNegative
    }

    @Deprecated("Do not use")
    fun isSet(): Boolean { // TODO: Create booking builder
        return title.isEmpty()
    }
}