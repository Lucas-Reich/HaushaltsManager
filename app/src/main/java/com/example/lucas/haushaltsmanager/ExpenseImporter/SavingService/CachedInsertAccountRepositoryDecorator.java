package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CachedInsertAccountRepositoryDecorator implements AccountDAO {
    private final AccountDAO repository;
    private final List<Account> cachedAccounts = new ArrayList<>();

    CachedInsertAccountRepositoryDecorator(AccountDAO repository) {
        this.repository = repository;
    }

    @Override
    public void insert(Account account) {
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
    public void delete(Account account) {
        repository.delete(account);
    }

    @Override
    public void update(Account account) {
        repository.update(account);
    }

    @Override
    public Account get(UUID index) {
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
                && one.getName().equals(other.getName())
                && one.getPrice().equals(other.getPrice());
    }
}
