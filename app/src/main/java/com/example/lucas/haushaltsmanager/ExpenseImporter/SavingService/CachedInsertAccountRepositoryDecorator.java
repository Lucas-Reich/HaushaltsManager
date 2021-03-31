package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CachedInsertAccountRepositoryDecorator implements AccountRepositoryInterface {
    private AccountRepositoryInterface repository;
    private List<Account> cachedAccounts = new ArrayList<>();

    CachedInsertAccountRepositoryDecorator(AccountRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public void insert(Account account) throws AccountCouldNotBeCreatedException {
        Account createdAccount = getAccountFromList(account);

        if (null != createdAccount) {
            return;
        }

        repository.insert(account);
        cachedAccounts.add(account);
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
    public void update(Account account) throws AccountNotFoundException {
        repository.update(account);
    }

    @Override
    public Account get(UUID index) throws AccountNotFoundException {
        return repository.get(index);
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
        return one.getId().equals(other.getId())
                && one.getTitle().equals(other.getTitle())
                && one.getBalance().equals(other.getBalance());
    }
}
