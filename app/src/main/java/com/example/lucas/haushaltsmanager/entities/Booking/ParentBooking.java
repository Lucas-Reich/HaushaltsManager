package com.example.lucas.haushaltsmanager.entities.Booking;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ParentBooking implements IBooking {
    private final UUID id;
    private Calendar date;
    private final String title;
    private final List<Booking> children;

    public ParentBooking(
            @NonNull String title
    ) {
        this(UUID.randomUUID(), title, Calendar.getInstance(), new ArrayList<>());
    }

    public ParentBooking(
            @NonNull UUID id,
            @NonNull String title,
            @NonNull Calendar date,
            @NonNull List<Booking> children
    ) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.children = children;
    }

    public UUID getId() {
        return id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentBooking)) {
            return false;
        }

        ParentBooking other = (ParentBooking) obj;

        return other.getTitle().equals(getTitle())
                && other.getChildren().equals(getChildren());
    }

    public Price getPrice() {
        return new Price(calcChildrenPrice());
    }

    public void addChild(Booking booking) {
        if (children.contains(booking)) {
            return;
        }

        children.add(booking);
    }

    public List<Booking> getChildren() {
        return children;
    }

    private double calcChildrenPrice() {
        double calcPrice = 0;
        for (Booking child : children) {

            calcPrice += child.getSignedPrice();
        }

        return calcPrice;
    }
}
