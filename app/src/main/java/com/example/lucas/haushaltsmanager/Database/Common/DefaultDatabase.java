package com.example.lucas.haushaltsmanager.Database.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

public class DefaultDatabase {
    private SQLiteDatabase db;

    public DefaultDatabase(SQLiteDatabase database) {
        db = database;
    }

    public DefaultDatabase(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        db = DatabaseManager.getInstance().openDatabase();
    }

    public IQueryResult query(IQuery query) {
        Cursor c = db.rawQuery(query.getQuery(), query.getDefinition());

        return new QueryResult(c);
    }

//    public IQueryResult insert(IQuery query) {
//        db.insert()
//    }

    // TODO: Hier kann ich eine Methode "queryInTransaction" erstellten, welche die angegebenen Queries in einer Transaktion ausführt
}
