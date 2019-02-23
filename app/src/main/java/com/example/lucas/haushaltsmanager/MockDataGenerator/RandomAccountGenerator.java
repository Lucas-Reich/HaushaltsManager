package com.example.lucas.haushaltsmanager.MockDataGenerator;


import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

public class RandomAccountGenerator {
    private Currency mCurrency;
    private Context mContext;

    public RandomAccountGenerator(Currency currency, Context context) {
        mCurrency = currency;
        mContext = context;
    }

    public void createAccounts(int count) {
        if (accountsExist()) {
            return;
        }

        AccountRepository accountRepository = new AccountRepository(mContext);

        for (; count > 0; count--) {
            accountRepository.create(makeAccount(
                    withRandomCurrency()
            ));
        }
    }

    private void saveInPreferences(Account account) {
        new ActiveAccountsPreferences(mContext).addAccount(account);
    }

    private boolean accountsExist() {
        return new AccountRepository(mContext).getAll().size() > 0;
    }

    private Account makeAccount(Currency currency) {
        return new Account(
                String.format("Konto %s", 1),
                0,
                currency
        );
    }

    private Currency withRandomCurrency() {
        return mCurrency;
    }
}
