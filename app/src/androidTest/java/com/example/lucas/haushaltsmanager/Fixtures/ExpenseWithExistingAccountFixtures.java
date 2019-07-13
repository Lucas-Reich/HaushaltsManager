package com.example.lucas.haushaltsmanager.Fixtures;

import android.database.sqlite.SQLiteDatabase;

public class ExpenseWithExistingAccountFixtures implements IFixtures {
    @Override
    public void apply(SQLiteDatabase db) {
        String insertQuery = "INSERT INTO BOOKINGS (expense_type, price, category_id, expenditure, title, date, notice, account_id, currency_id, hidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[][] entries = getEntries();
        for (String[] entry : entries) {
            db.rawQuery(insertQuery, entry);
        }
    }

    @Override
    public void revert(SQLiteDatabase db) {
        db.rawQuery("DELETE FROM BOOKINGS;", new String[0]);
    }

    private String[][] getEntries() {
        return new String[][]{
                {"NORMAL_EXPENSE", "150", "-1", "1", "Eine Ausgabe", "1562504532617", "Eine Notiz", "3", "-1", "0"}
        };
    }
}

