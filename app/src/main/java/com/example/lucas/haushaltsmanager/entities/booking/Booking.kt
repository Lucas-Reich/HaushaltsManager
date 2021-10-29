package com.example.lucas.haushaltsmanager.entities.booking

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Account
import com.example.lucas.haushaltsmanager.entities.Price
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "bookings")
@Parcelize
class Booking(
    @PrimaryKey private val id: UUID,
    private var title: String,
    private var price: Price,
    private var date: Calendar,
    @ColumnInfo(name = "category_id") var categoryId: UUID,
    @ColumnInfo(name = "account_id") var accountId: UUID
) : IBooking, Parcelable {
    constructor(
        title: String,
        price: Price,
        categoryId: UUID,
        accountId: UUID
    ) : this(UUID.randomUUID(), title, price, Calendar.getInstance(), categoryId, accountId)

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
        return price.absoluteValue
    }

    fun getSignedPrice(): Double {
        return price.price
    }

    fun isExpenditure(): Boolean {
        return price.isNegative
    }

    fun setAccount(account: Account) {
        this.accountId = account.id
    }

    @Deprecated("Do not use")
    fun isSet(): Boolean { // TODO: Create booking builder
        return title.isEmpty()
    }
}