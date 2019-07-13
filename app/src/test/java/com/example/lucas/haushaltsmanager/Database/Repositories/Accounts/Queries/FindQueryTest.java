package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class FindQueryTest {

    @Test
    public void findQueryReturnsCorrectSqlStatement() {
        String expectedQuery = "SELECT ACCOUNTS.account_id, ACCOUNTS.acc_name, ACCOUNTS.balance, CURRENCIES.currency_id, CURRENCIES.cur_name, CURRENCIES.short_name, CURRENCIES.symbol FROM ACCOUNTS JOIN CURRENCIES USING(currency_id) WHERE ACCOUNTS.account_id = ?";

        assertSame(
                expectedQuery,
                new FindQuery(ExpensesDbHelper.INVALID_INDEX).getQuery()
        );
    }

    @Test
    public void findQueryReturnsCorrectQueryArguments() {
        long expectedArgument = -1;

        String[] queryArguments = new FindQuery(expectedArgument).getDefinition();

        assertEquals(1, queryArguments.length);
        assertEquals(Long.toString(expectedArgument), queryArguments[0]);
    }
}
