package com.example.lucas.haushaltsmanager.entities.booking

import com.example.lucas.haushaltsmanager.entities.Price
import java.util.*

class ParentBooking(
    private val id: UUID,
    private var date: Calendar,
    private val title: String,
    val children: ArrayList<Booking>
) : IBooking {
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

        children.add(booking)
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

    override fun getPrice(): Price {
        var price = 0.0
        for (child in children) {
            price += child.price.signedValue
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
}