package com.example.lucas.haushaltsmanager.Entities.Expense;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;
import java.util.UUID;

public interface IBooking {
    UUID getId();

    Calendar getDate();

    void setDate(Calendar date);

    String getTitle();

    Price getPrice();
}
