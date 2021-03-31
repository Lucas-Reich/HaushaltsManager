package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.Account;

public class AccountCouldNotBeCreatedException extends EntityCouldNotBeCreatedException {
    public AccountCouldNotBeCreatedException(Account account, Throwable previous) {
        super(String.format(
                "Could not create account with id '%s', reason: %s",
                account.getId().toString(),
                previous.getMessage()
        ), previous);
    }
}
