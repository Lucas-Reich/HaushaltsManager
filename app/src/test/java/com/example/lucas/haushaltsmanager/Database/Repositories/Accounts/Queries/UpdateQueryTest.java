package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

import junit.framework.TestCase;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UpdateQueryTest {

    @Test
    public void updateQueryReturnsCorrectSqlStatement() {
        String expectedQuery = "UPDATE ACCOUNTS SET acc_name = ?, balance = ?, currency_id = ? WHERE account_id = ?";

        TestCase.assertEquals(
                expectedQuery,
                new UpdateQuery(null).getQuery()
        );
    }

    @Test
    public void updateQueryReturnsCorrectArguments() {
        Account account = getDummyAccount();

        String[] arguments = new UpdateQuery(account).getDefinition();

        assertEquals(4, arguments.length);
        assertExpectedArguments(account, arguments);
    }

    private Account getDummyAccount() {
        return new Account(
                ExpensesDbHelper.INVALID_INDEX,
                "Mein Konto",
                313.313,
                new Currency("Euro", "EUR", "€")
        );
    }

    private void assertExpectedArguments(Account account, String[] arguments) {
        Price accountBalance = account.getBalance();

        assertEquals(account.getTitle(), arguments[0]);
        assertEquals(Double.toString(accountBalance.getSignedValue()), arguments[1]);
        assertEquals(Long.toString(accountBalance.getCurrency().getIndex()), arguments[2]);
        assertEquals(Long.toString(account.getIndex()), arguments[3]);
    }
}
