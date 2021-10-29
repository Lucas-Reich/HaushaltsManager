package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

public interface ChildExpenseRepositoryInterface {
    Booking addChildToBooking(Booking childExpense, IBooking parentBooking) throws AddChildToChildException;

    Booking extractChildFromBooking(Booking childExpense) throws ChildExpenseNotFoundException;

    void delete(Booking childExpense) throws CannotDeleteChildExpenseException;
}
