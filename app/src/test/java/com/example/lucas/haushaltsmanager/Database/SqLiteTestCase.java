package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lucas.haushaltsmanager.Database.Common.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.Migrations.IMigration;
import com.example.lucas.haushaltsmanager.Database.Migrations.MigrationHelper;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class SqLiteTestCase {
    private static SQLiteDatabase db;

    @BeforeClass
    public static void loadToMemory() {
        SQLiteDatabase.OpenParams.Builder test = new SQLiteDatabase.OpenParams.Builder();
//        db = SQLiteDatabase.createInMemory(test.build());


        db = SQLiteDatabase.openOrCreateDatabase("", null);

        bringDbUpToDate();
    }

    @AfterClass
    public static void teardown() {
        db.close();
    }

    public abstract void addFixtures();

    @Before
    public void setUp() {
        clearTables();

        insertFixtures();
    }

    public DefaultDatabase getDefaultDatabase() {
        return new DefaultDatabase(db);
    }

    private static void bringDbUpToDate() {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (IMigration migration : migrations) {
            migration.apply(db);
        }
    }

    private void clearTables() {
        db.rawQuery("DELETE FROM BOOKINGS;", new String[]{});
        db.rawQuery("DELETE FROM BOOKING_TAGS;", new String[]{});
        db.rawQuery("DELETE FROM RECURRING_BOOKIGNS;", new String[]{});
        db.rawQuery("DELETE FROM TEMPLATE_BOOKINGS;", new String[]{});

        db.rawQuery("DELETE FROM CATEGORIES;", new String[]{});
        db.rawQuery("DELETE FROM CHILD_CATEGORIES;", new String[]{});

        db.rawQuery("DELETE FROM ACCOUNTS;", new String[]{});

        db.rawQuery("DELETE FROM CURRENCIES;", new String[]{});

        db.rawQuery("DELETE FROM TAGS;", new String[]{});
    }

    private void insertFixtures() {
        // TODO: do smth
    }
}
