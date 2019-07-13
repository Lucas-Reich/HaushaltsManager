package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;

public class CannotDeleteAccountException extends CouldNotDeleteEntityException {
    public CannotDeleteAccountException(Account account) {
        super("Account " + account.getTitle() + " cannot be deleted.");
    }
}
