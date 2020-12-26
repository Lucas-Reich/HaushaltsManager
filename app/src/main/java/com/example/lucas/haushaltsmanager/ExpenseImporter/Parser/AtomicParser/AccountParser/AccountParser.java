package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.Collections;
import java.util.List;

public class AccountParser implements IParser<Account> {
    public static final IRequiredField ACCOUNT_TITLE_KEY = new Title();

    private final Currency mainCurrency;

    public AccountParser(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    @Override
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(ACCOUNT_TITLE_KEY);
    }

    public Account parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String accountTitle = line.getAsString(mapping.getMappingForKey(ACCOUNT_TITLE_KEY));

        assertNotEmpty(accountTitle);

        return new Account(
                accountTitle,
                new Price(0, mainCurrency)
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Account.class);
    }
}
