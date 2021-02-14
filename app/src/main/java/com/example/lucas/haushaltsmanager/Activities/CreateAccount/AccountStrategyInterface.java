package com.example.lucas.haushaltsmanager.Activities.CreateAccount;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

public interface AccountStrategyInterface {
    Account build(BundleUtils bundle);

    boolean save(Account account);
}
