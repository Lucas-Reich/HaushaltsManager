package com.example.lucas.haushaltsmanager.entities.booking

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Price
import java.util.*

@Entity(tableName = "parent_bookings")
class ParentBooking(
    @PrimaryKey var id: UUID,
    private var date: Calendar,
    val title: String,
    @Ignore val children: ArrayList<Booking>
): IBooking {

    init {
        for(child in children) {
            child.parentId = id
        }
    }

    constructor(title: String) : this(
        UUID.randomUUID(),
        Calendar.getInstance(),
        title,
        ArrayList<Booking>()
    )

    fun addChildren(bookings: List<Booking>) {
        for (booking in bookings) {
            addChild(booking)
        }
    }

    fun addChild(booking: Booking) {
        if (children.contains(booking)) {
            return
        }

        booking.parentId = id;
        children.add(booking)
    }

    fun getPrice(): Price {
        var price = 0.0
        for (child in children) {
            price += child.price.price
        }

        return Price(price)
    }

    override fun getDate(): Calendar {
        return date
    }

    fun setDate(date: Calendar) {
        this.date = date
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ParentBooking) {
            return false
        }

        return title == other.title
                && children == other.children
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }
}