package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class AccountTransformerTest {
    private AccountTransformer transformer;

    @Before
    public void setUp() {
        transformer = new AccountTransformer();
    }

    @Test
    public void testCursorToAccountWithValidCursorShouldSucceed() {
        // Arrange
        final Account expectedAccount = getSimpleAccount();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("id", expectedAccount.getId().toString());
            put("name", expectedAccount.getTitle());
            put("balance", expectedAccount.getBalance().getUnsignedValue());
        }});

        // Act
        Account fetchedAccount = transformer.transform(cursor);

        // Assert
        assertEquals(expectedAccount, fetchedAccount);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToAccountWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        // Arrange
        final Account expectedAccount = getSimpleAccount();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("id", expectedAccount.getId().toString());
            // Account name is not in Cursor
            put("balance", expectedAccount.getBalance().getUnsignedValue());
        }});

        // Act
        transformer.transform(cursor);
    }

    private Cursor createCursor(Map<String, Object> values) {
        String[] columns = values.keySet().toArray(new String[0]);
        MatrixCursor cursor = new MatrixCursor(columns);

        MatrixCursor.RowBuilder builder = cursor.newRow();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        cursor.moveToFirst();
        return cursor;
    }

    private Account getSimpleAccount() {
        return new Account(
                "Bank account",
                new Price(7653)
        );
    }
}
