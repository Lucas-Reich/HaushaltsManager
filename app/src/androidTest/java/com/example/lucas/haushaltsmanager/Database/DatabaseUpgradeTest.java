package com.example.lucas.haushaltsmanager.Database;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
/**
 * Quelle: https://riggaroo.co.za/automated-testing-sqlite-database-upgrades-android/
 */
public class DatabaseUpgradeTest {
    @Before
    public void setUp() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(
                InstrumentationRegistry.getTargetContext()
        ));
    }

    @After
    public void teardown() {
        DatabaseManager.clearInstance();
    }

    /**
     * This test runs through all the database versions from the /androidTest/assets/ folder.
     * CSVFileReader has to be in format database_v<VERSION_NUMBER>.db
     *
     * @throws IOException if the database cannot be copied.
     */
    @Test
    public void testDatabaseUpgrades() throws IOException {
        for (int currentVersion = 1; currentVersion <= ExpensesDbHelper.DB_VERSION; currentVersion++) {
            DatabaseManager.clearInstance();

            loadDatabaseWithVersion(currentVersion);

            assertEquals(ExpensesDbHelper.DB_VERSION, getCurrentDbVersion());
        }

    }

    private int getCurrentDbVersion() {
        return DatabaseManager
                .getInstance()
                .openDatabase()
                .getVersion();
    }

    private void loadDatabaseWithVersion(int version) throws IOException {
        String dbPath = InstrumentationRegistry.getTargetContext().getDatabasePath(ExpensesDbHelper.DB_NAME).getAbsolutePath();

        String dbName = String.format("database_v%d.db", version);
        InputStream mInput = InstrumentationRegistry.getContext().getAssets().open(dbName);

        File db = new File(dbPath);
        if (!db.exists()) {
            db.getParentFile().mkdirs();
            db.createNewFile();
        }
        OutputStream mOutput = new FileOutputStream(dbPath);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
}
