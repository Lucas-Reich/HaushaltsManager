package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.entities.Account;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CachedInsertAccountRepositoryDecorator implements AccountDAO {
    private final AccountDAO repository;
    private final List<Account> cachedAccounts = new ArrayList<>();

    CachedInsertAccountRepositoryDecorator(AccountDAO repository) {
        this.repository = repository;
    }

    @Override
    public void insert(@NonNull Account account) {
        Account createdAccount = getAccountFromList(account);

        if (null != createdAccount) {
            return;
        }

        repository.insert(account);
        cachedAccounts.add(account);
    }

    @Override
    @NonNull
    public List<Account> getAll() {
        return repository.getAll();
    }

    @Override
    public void delete(@NonNull Account account) {
        repository.delete(account);
    }

    @Override
    public void update(@NotNull Account account) {
        repository.update(account);
    }

    @Override
    @NonNull
    public Account get(@NonNull UUID index) {
        return repository.get(index);
    }

    @Override
    @NonNull
    public Account getByName(@NonNull String accountName) {
        Account account = getAccountByNameFromCache(accountName);

        if (null != account) {
            return account;
        }

        return repository.getByName(accountName);
    }

    @Nullable
    private Account getAccountByNameFromCache(String name) {
        for (Account account : cachedAccounts) {
            if (!name.equals(account.getName())) {
                continue;
            }

            return account;
        }

        return null;
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
        return one.getName().equals(other.getName())
                && one.getBalance().equals(other.getBalance());
    }
}
