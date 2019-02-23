package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class CannotDeleteBookingTagException extends CouldNotDeleteEntityException {
    public CannotDeleteBookingTagException(ExpenseObject expense, Tag tag) {
        super("Tag " + tag.getName() + " or Booking " + expense.getTitle() + " does not exist.");
    }
}
