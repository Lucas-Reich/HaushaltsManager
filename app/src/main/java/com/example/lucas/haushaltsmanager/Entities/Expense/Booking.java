package com.example.lucas.haushaltsmanager.Entities.Expense;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;

public interface Booking {
    // TODO: Was macht eine Buchung aus?
    Calendar getDate();

    String getTitle();

    Price getPrice();

    Currency getCurrency();
}
