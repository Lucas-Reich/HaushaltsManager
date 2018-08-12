package com.example.lucas.haushaltsmanager.Database.Exceptions;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class EntityNotExistingException extends Exception {

    public EntityNotExistingException(String message) {

        super(message);
    }
}
