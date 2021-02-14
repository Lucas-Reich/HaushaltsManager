package com.example.lucas.haushaltsmanager.Activities.CreateAccount;

import com.example.lucas.haushaltsmanager.Activities.CreateAccountActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public class UpdateAccountStrategy implements AccountStrategyInterface {
    private final AccountRepositoryInterface repository;

    public UpdateAccountStrategy(AccountRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public Account build(BundleUtils bundle) {
        return (Account) bundle.getParcelable(CreateAccountActivity.INTENT_ACCOUNT, null);
    }

    @Override
    public boolean save(Account account) {
        try {
            repository.update(account);

            return true;
        } catch (AccountNotFoundException e) {
            return false;
        }
    }
}
