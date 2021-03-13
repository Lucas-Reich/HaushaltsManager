package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.List;

public interface AccountRepositoryInterface {
    Account get(long index) throws AccountNotFoundException;

    List<Account> getAll();

    Account insert(Account entity);

    void delete(Account account) throws CannotDeleteAccountException;

    void update(Account account) throws AccountNotFoundException;

    void closeDatabase();
}
