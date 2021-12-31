package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.AccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.Collections;
import java.util.List;

public class AccountParser implements IParser<Account> {
    public static final IRequiredField ACCOUNT_TITLE_KEY = new AccountTitle();

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(ACCOUNT_TITLE_KEY);
    }

    @Override
    @NonNull
    public Account parse(@NonNull Line line, @NonNull MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String accountTitle = line.getAsString(mapping.getMappingForKey(ACCOUNT_TITLE_KEY));

        assertNotEmpty(accountTitle);

        return new Account(
                accountTitle,
                new Price(0)
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Account.class);
    }
}
