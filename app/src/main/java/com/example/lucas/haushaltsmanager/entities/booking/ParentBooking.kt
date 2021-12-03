package com.example.lucas.haushaltsmanager.entities.booking

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.entities.Price
import java.util.*

@Entity(tableName = "parent_bookings")
class ParentBooking(
    @PrimaryKey var id: UUID, // Cannot make this final for some reason
    var date: Calendar,
    val title: String,
    @Ignore val children: ArrayList<Booking>
) {
    constructor(title: String) : this(
        UUID.randomUUID(),
        Calendar.getInstance(),
        title,
        ArrayList<Booking>()
    )

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