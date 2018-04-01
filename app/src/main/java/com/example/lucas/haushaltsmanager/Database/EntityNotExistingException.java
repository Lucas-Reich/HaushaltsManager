package com.example.lucas.haushaltsmanager.Database;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class EntityNotExistingException extends Exception {

    public EntityNotExistingException(String message) {

        super(message);
    }

    public EntityNotExistingException(Account account) {

        super("Account " + account.getName() + " does not exist in the database");
    }

    public EntityNotExistingException(Category category) {

        super("Category " + category.getName() + " does not exist in the database");
    }

    public EntityNotExistingException(ExpenseObject expense) {

        super("Expense " + expense.getName() + " does not exist in the database");
    }

    public EntityNotExistingException(Tag tag) {

        super("Tag " + tag.getName() + " does not exist int the database");
    }

    public EntityNotExistingException(Currency currency) {

        super("Currency " + currency.getName() + " does not exist in the database");
    }
}
