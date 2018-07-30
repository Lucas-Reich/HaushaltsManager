package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public class CannotDeleteCurrencyException extends CouldNotDeleteEntityException {
    public CannotDeleteCurrencyException(Currency currency) {
        super("Currency " + currency.getName() + " cannot be deleted.");
    }
}
