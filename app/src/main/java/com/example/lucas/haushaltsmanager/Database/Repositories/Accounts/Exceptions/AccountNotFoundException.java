package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class AccountNotFoundException extends EntityNotExistingException {

    public AccountNotFoundException(long accountId) {
        super("Could not find Account with id " + accountId + ".");
    }
}
