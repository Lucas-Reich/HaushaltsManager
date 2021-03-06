package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CachedInsertAccountRepositoryDecorator implements AccountRepositoryInterface {
    private AccountRepositoryInterface repository;
    private List<Account> cachedAccounts = new ArrayList<>();

    CachedInsertAccountRepositoryDecorator(AccountRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public Account insert(Account account) {
        Account createdAccount = getAccountFromList(account);

        if (null == createdAccount) {
            createdAccount = repository.insert(account);
            cachedAccounts.add(createdAccount);
        }

        return createdAccount;
    }

    @Override
    public Account get(long index) throws AccountNotFoundException {
        return repository.get(index);
    }

    @Override
    public List<Account> getAll() {
        return repository.getAll();
    }

    @Override
    public void delete(Account account) throws CannotDeleteAccountException {
        repository.delete(account);
    }

    @Override
    public boolean exists(Account account) {
        return repository.exists(account);
    }

    @Override
    public void closeDatabase() {
        repository.closeDatabase();
    }

    @Override
    public void update(Account account) throws AccountNotFoundException {
        repository.update(account);
    }

    @Override
    public boolean isCurrencyAttachedToAccount(Currency currency) {
        return repository.isCurrencyAttachedToAccount(currency);
    }

    @Nullable
    private Account getAccountFromList(Account account) {
        for (Account existingAccount : cachedAccounts) {
            if (areEquals(existingAccount, account)) {
                return existingAccount;
            }
        }

        return null;
    }

    private boolean areEquals(Account one, Account other) {
        return one.getTitle().equals(other.getTitle())
                && one.getBalance().equals(other.getBalance());
    }
}
