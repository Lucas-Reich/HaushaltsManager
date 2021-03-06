package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.BaseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public interface AccountRepositoryInterface extends BaseRepository<Account> {

    Account get(long index) throws AccountNotFoundException;

    void delete(Account account) throws CannotDeleteAccountException;

    void update(Account account) throws AccountNotFoundException;

    boolean isCurrencyAttachedToAccount(Currency currency);
}
