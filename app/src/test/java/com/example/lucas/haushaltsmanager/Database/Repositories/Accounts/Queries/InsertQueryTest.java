package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

import junit.framework.TestCase;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class InsertQueryTest {

    @Test
    public void insertQueryReturnsCorrectSqlStatement() {
        String expectedQuery = "INSERT INTO ACCOUNTS (acc_name, balance, currency_id) VALUES (?, ?, ?)";

        TestCase.assertEquals(
                expectedQuery,
                new InsertQuery(null).getQuery()
        );
    }

    @Test
    public void insertQueryReturnsCorrectQueryArguments() {
        Account account = getDummyAccount();

        String[] arguments = new InsertQuery(account).getDefinition();

        assertEquals(3, arguments.length);
        assertExpectedArguments(account, arguments);
    }

    private Account getDummyAccount() {
        return new Account(
                ExpensesDbHelper.INVALID_INDEX,
                "Mein Konto",
                3333.5,
                new Currency("Euro", "EUR", "€")
        );
    }

    private void assertExpectedArguments(Account account, String[] arguments) {
        Price accountBalance = account.getBalance();

        assertEquals(account.getTitle(), arguments[0]);
        assertEquals(Double.toString(accountBalance.getSignedValue()), arguments[1]);
        assertEquals(Long.toString(accountBalance.getCurrency().getIndex()), arguments[2]);
    }
}
