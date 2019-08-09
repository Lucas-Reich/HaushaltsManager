package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

public class AccountParser implements IObjectParser<Account> {
    public static final String ACCOUNT_TITLE_KEY = "account_title";

    private Currency mainCurrency;

    public AccountParser(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    public Account parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String accountTitle = line.getAsString(mapping.getMappingForKey(ACCOUNT_TITLE_KEY));

        assertNotEmpty(accountTitle);

        return new Account(
                accountTitle,
                0,
                mainCurrency
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Account.class);
    }
}
