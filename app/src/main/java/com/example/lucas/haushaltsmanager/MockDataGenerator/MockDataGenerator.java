package com.example.lucas.haushaltsmanager.MockDataGenerator;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public class MockDataGenerator {
    public void createMockData(Context context) {
        // TODO: Kann ich ein ProgressBar popup anzeigen, welches anzeigt wie viele Buchungen noch erstellt werden müssen
        Currency currency = getMainCurrency(context);

        generateAccounts(3, context, currency);
        generateCategories(6, context);
        generateExpenses(1000, context, currency);
    }

    private void generateAccounts(int amount, Context context, Currency currency) {
        RandomAccountGenerator generator = new RandomAccountGenerator(currency, context);
        generator.createAccounts(amount);
    }

    private void generateCategories(int amount, Context context) {
        // TODO: Es sollen nur Kategorien generiert werden, wenn es noch keine gibt
        RandomCategoryGenerator generator = new RandomCategoryGenerator(context);
        generator.createdCategories(amount, context);
    }

    private void generateExpenses(int amount, Context context, Currency currency) {
        RandomExpenseGenerator generator = new RandomExpenseGenerator(context, currency);
        generator.createExpenses(amount, context);
    }

    private Currency getMainCurrency(Context context) {
        // TODO: Es soll nur eine neue Währungen erstellt werden, wenn es noch keine gibt
        RandomCurrencyGenerator generator = new RandomCurrencyGenerator();
        generator.createCurrencies(1, context);

        return new CurrencyRepository(context).getAll().get(0);
    }
}
