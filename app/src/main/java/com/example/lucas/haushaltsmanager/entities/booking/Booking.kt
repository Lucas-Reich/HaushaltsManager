package com.example.lucas.haushaltsmanager.entities.booking

import android.os.Parcelable
import com.example.lucas.haushaltsmanager.entities.Category
import com.example.lucas.haushaltsmanager.entities.Price
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Booking(
    private val id: UUID,
    private var title: String,
    private var price: Price,
    private var date: Calendar,
    var category: Category,
    var accountId: UUID
) : IBooking, Parcelable {
    constructor(
        title: String,
        price: Price,
        category: Category,
        accountId: UUID
    ) : this(UUID.randomUUID(), title, price, Calendar.getInstance(), category, accountId)

    override fun equals(other: Any?): Boolean {
        if (other !is Booking) {
            return false
        }

        return title == other.title
                && price == price
                && accountId == accountId
                && date == date
                && category == category
    }

    override fun toString(): String {
        return "$id $title ${price.unsignedValue}"
    }

    override fun getId(): UUID {
        return id
    }

    override fun getDate(): Calendar {
        return date
    }

    override fun setDate(date: Calendar) {
        this.date = date
    }

    override fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    override fun getPrice(): Price {
        return price
    }

    fun setPrice(price: Price) {
        this.price = price
    }

    fun getDisplayableDateTime(): String {
        return DateFormat.getDateInstance(DateFormat.SHORT)
            .format(Date(date.timeInMillis))
    }

    fun getDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date.time)
    }

    fun getUnsignedPrice(): Double {
        return price.unsignedValue
    }

    fun getSignedPrice(): Double {
        return price.signedValue
    }

    fun isExpenditure(): Boolean {
        return price.isNegative
    }

    @Deprecated("Do not use")
    fun isSet(): Boolean {
        return title.isEmpty()
                && category.isSet()
    }
}