package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class CannotDeleteBookingTagException extends CouldNotDeleteEntityException {
    public CannotDeleteBookingTagException(ExpenseObject expense, Tag tag) {
        super("Tag " + tag.getName() + " or Expense " + expense.getTitle() + " does not exist.");
    }
}
