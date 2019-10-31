package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DeleteQueryTest {

    @Test
    public void deleteQueryReturnsCorrectSqlStatement() {
        String expectedSql = "DELETE FROM ACCOUNTS WHERE account_id = ?";

        assertSame(
                expectedSql,
                new DeleteQuery(ExpensesDbHelper.INVALID_INDEX).getQuery()
        );
    }

    @Test
    public void deleteQueryReturnsCorrectQueryArguments() {
        Account account = getDummyAccount();

        String[] arguments = new DeleteQuery(account.getIndex()).getDefinition();

        assertEquals(1, arguments.length);
        assertEquals(Long.toString(account.getIndex()), arguments[0]);
    }

    private Account getDummyAccount() {
        return new Account(
                ExpensesDbHelper.INVALID_INDEX,
                "Mein Konto",
                5000,
                new Currency("Euro", "EUR", "€")
        );
    }
}
