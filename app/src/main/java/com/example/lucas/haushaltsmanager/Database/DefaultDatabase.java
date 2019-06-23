package com.example.lucas.haushaltsmanager.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.QueryResult;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.TagTransformer;

public class DefaultDatabase {
    private SQLiteDatabase db;

    public DefaultDatabase(SQLiteDatabase database) {
        db = database;
    }

    public QueryResult query(QueryInterface query) {
        Cursor c = db.rawQuery(query.getQuery(), query.getDefinition());

        return new QueryResult(c, new TagTransformer());
    }
}
