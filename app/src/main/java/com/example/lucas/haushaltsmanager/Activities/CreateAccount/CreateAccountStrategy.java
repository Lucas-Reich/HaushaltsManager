package com.example.lucas.haushaltsmanager.Activities.CreateAccount;

import android.content.Context;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferencesInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public class CreateAccountStrategy implements AccountStrategyInterface {
    private final AccountRepositoryInterface repository;

    public CreateAccountStrategy(AccountRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public Account build(BundleUtils bundle) {
        Price defaultPrice = new Price(0, getDefaultCurrency());

        return new Account(
                "",
                defaultPrice
        );
    }

    @Override
    public boolean save(Account account) {
        account = repository.insert(account);

        getAccountPreferences().addAccount(account);

        return true;
    }

    private Currency getDefaultCurrency() {
        Context context = app.getContext();

        return new UserSettingsPreferences(context).getMainCurrency();
    }

    private ActiveAccountsPreferencesInterface getAccountPreferences() {
        Context context = app.getContext();

        return new AddAndSetDefaultDecorator(new ActiveAccountsPreferences(context), context);
    }
}

