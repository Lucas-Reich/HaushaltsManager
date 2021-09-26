package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.UUID;

public class AccountNotFoundException extends EntityNotExistingException {

    public AccountNotFoundException(UUID accountId) {
        super("Could not find Account with id " + accountId.toString() + ".");
    }
}
