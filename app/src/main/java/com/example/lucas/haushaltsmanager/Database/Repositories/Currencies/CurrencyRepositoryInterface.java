package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.List;

public interface CurrencyRepositoryInterface {
    Currency get(long currencyId) throws CurrencyNotFoundException;

    List<Currency> getAll();

    // TODO: Method is only used in testing
    Currency insert(Currency currency);
}
