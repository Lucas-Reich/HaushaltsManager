package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepositoryInterface {
    Account get(UUID index) throws AccountNotFoundException;

    List<Account> getAll();

    void insert(Account entity) throws AccountCouldNotBeCreatedException;

    void delete(Account account) throws CannotDeleteAccountException;

    void update(Account account) throws AccountNotFoundException;
}
