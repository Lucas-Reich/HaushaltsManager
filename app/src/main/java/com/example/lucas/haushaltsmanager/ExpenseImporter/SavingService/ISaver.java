package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public interface ISaver {
    void revert();

    void finish();

    void persist(Booking booking, Account account);
}
