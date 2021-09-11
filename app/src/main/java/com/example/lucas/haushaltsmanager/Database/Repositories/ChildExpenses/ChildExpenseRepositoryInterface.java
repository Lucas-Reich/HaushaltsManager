package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;

import java.util.List;
import java.util.UUID;

public interface ChildExpenseRepositoryInterface {
    boolean exists(Booking childExpense);

    Booking addChildToBooking(Booking childExpense, IBooking parentBooking) throws AddChildToChildException;

    Booking extractChildFromBooking(Booking childExpense) throws ChildExpenseNotFoundException;

    Booking get(UUID childExpenseId) throws ChildExpenseNotFoundException;

    List<Booking> getAll(UUID parentExpenseId);

    void insert(ParentBooking parent, Booking child);

    void insert(Booking parentExpense, Booking childExpense);

    void update(Booking childExpense) throws ChildExpenseNotFoundException;

    void delete(Booking childExpense) throws CannotDeleteChildExpenseException;

    void hide(Booking childExpense) throws ChildExpenseNotFoundException;

    void closeDatabase();
}
