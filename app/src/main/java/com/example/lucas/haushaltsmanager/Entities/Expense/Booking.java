package com.example.lucas.haushaltsmanager.Entities.Expense;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;
import java.util.UUID;

public interface Booking {
    UUID getId();

    // TODO: Was macht eine Buchung aus?
    Calendar getDate();

    void setDate(Calendar date);

    String getTitle();

    Price getPrice();
}
