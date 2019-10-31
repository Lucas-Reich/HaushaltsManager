package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import junit.framework.TestCase;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class GetAllTest {

    @Test
    public void listReturnsCorrectSqlStatement() {
        String expectedQuery = "SELECT ACCOUNTS.account_id, ACCOUNTS.acc_name, ACCOUNTS.balance, CURRENCIES.currency_id, CURRENCIES.cur_name, CURRENCIES.short_name, CURRENCIES.symbol FROM ACCOUNTS JOIN CURRENCIES USING (currency_id)";

        TestCase.assertEquals(
                expectedQuery,
                new ListQuery().getQuery()
        );
    }

    @Test
    public void listReturnsCorrectArguments() {
        String[] arguments = new ListQuery().getDefinition();

        assertEquals(0, arguments.length);
    }
}
