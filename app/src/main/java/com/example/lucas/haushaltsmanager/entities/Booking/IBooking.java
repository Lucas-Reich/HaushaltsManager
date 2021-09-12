package com.example.lucas.haushaltsmanager.entities.Booking;

import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.Calendar;
import java.util.UUID;

public interface IBooking {
    UUID getId();

    Calendar getDate();

    void setDate(Calendar date);

    String getTitle();

    Price getPrice();
}
