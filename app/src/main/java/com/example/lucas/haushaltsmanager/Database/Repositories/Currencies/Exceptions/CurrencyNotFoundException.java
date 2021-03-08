package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class CurrencyNotFoundException extends EntityNotExistingException {
    public CurrencyNotFoundException(long currencyId) {
        super("Could not find Currency with id " + currencyId + ".");
    }
}
