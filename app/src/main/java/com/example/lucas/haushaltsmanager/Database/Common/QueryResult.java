package com.example.lucas.haushaltsmanager.Database.Common;

import android.database.Cursor;

public class QueryResult implements IQueryResult {
    private Cursor cursor;

    public QueryResult(Cursor c) {
        cursor = c;
    }

    @Override
    public boolean moveToNext() {
        return cursor.moveToNext();
    }

    @Override
    public Cursor getCurrent() {
        return cursor;
    }

    @Override
    public void close() {
        cursor.close();
    }
}
