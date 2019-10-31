package com.example.lucas.haushaltsmanager.Fixtures;

import android.database.sqlite.SQLiteDatabase;

public class AccountFixtures implements IFixtures {
    @Override
    public void apply(SQLiteDatabase db) {
        String insertQuery = "INSERT INTO ACCOUNTS (acc_name, balance, currency_id) VALUES (?, ?, ?)";

        String[][] entries = getEntries();
        for (String[] arguments : entries) {
            db.rawQuery(insertQuery, arguments);
        }
    }

    @Override
    public void revert(SQLiteDatabase db) {
        db.rawQuery("DELETE FROM ACCOUNTS", new String[]{});
    }

    private String[][] getEntries() {
        return new String[][]{
                new String[]{"Konto 1", "0", "1"},
                new String[]{"Konto 2", "313.3", "1"},
                new String[]{"Konto 3", "-500", "2"},
        };
    }
}
