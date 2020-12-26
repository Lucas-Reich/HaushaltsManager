package com.example.lucas.haushaltsmanager.MockDataGenerator;


import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public class RandomCurrencyGenerator {
    public void createCurrencies(int count, Context context) {
        CurrencyRepository currencyRepository = new CurrencyRepository(context);

        for (; count >= 0; count--) {
            currencyRepository.insert(makeCurrency());
        }
    }

    private Currency makeCurrency() {
        return new Currency(
                "Euro",
                "EUR",
                "€"
        );
    }
}
