package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupHandler {
    private static final String TAG = BackupHandler.class.getSimpleName();
    //.SaveDataFile
    private static final String mBackupFileExtension = "sdf";

    public static final String AUTOMATIC_BACKUP_REGEX = String.format("([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))_Backup.%s", mBackupFileExtension);
    public static final String BACKUP_EXTENSION_REGEX = String.format(".*.%s", mBackupFileExtension);

    public boolean createBackup(
            @Nullable String backupName,
            @Nullable Directory backupDir,
            Context context
    ) {
        Log.i(TAG, String.format("Creating Backup: %s", backupName));

        return FileUtils.copy(
                getDatabasePath(context),
                backupDir == null ? getBackupDirectory(context) : backupDir,
                createBackupFileName(backupName)
        );
    }

    public boolean restoreBackup(File backup, Context context) {
        if (!isValidSQLite(backup))
            return false;

        if (!isCorrectDatabaseVersion(backup))
            return false;

        if (!hasCorrectStructure(backup))
            return false;

        Log.i(TAG, String.format("Restoring Backup: %s", backup.getName()));

        return FileUtils.copy(
                backup,
                getBackupDirectory(context),
                ExpensesDbHelper.DB_NAME
        );
    }

    private boolean hasCorrectStructure(File database) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(database.getPath(), null, 0);
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            c.moveToFirst();

            List<String> tableName = new ArrayList<>();
            while (!c.isAfterLast()) {
                tableName.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
            db.close();
            c.close();

            return tableName.contains(ExpensesDbHelper.TABLE_ACCOUNTS)
                    && tableName.contains(ExpensesDbHelper.TABLE_BOOKINGS)
                    && tableName.contains(ExpensesDbHelper.TABLE_BOOKINGS_TAGS)
                    && tableName.contains(ExpensesDbHelper.TABLE_CATEGORIES)
                    && tableName.contains(ExpensesDbHelper.TABLE_CHILD_CATEGORIES)
                    && tableName.contains(ExpensesDbHelper.TABLE_CURRENCIES)
                    && tableName.contains(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS)
                    && tableName.contains(ExpensesDbHelper.TABLE_TAGS)
                    && tableName.contains(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCorrectDatabaseVersion(File database) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(database.getPath(), null, 0);
            boolean sameVersion = db.getVersion() == ExpensesDbHelper.DB_VERSION;
            db.close();

            return sameVersion;
        } catch (SQLiteException e) {

            return false;
        }
    }

    /**
     * Überprüft ob die angegebene Datei eine SQLite Datenbank enthält.
     * Quelle: https://stackoverflow.com/a/39751165/9376633
     */
    private boolean isValidSQLite(File file) {
        if (!file.exists() || !file.canRead())
            return false;

        try {
            FileReader fr = new FileReader(file);
            char[] buffer = new char[16];

            fr.read(buffer, 0, 16);
            String str = String.valueOf(buffer);
            fr.close();

            return str.equals("SQLite format 3\u0000");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteBackupsAboveThreshold(Directory directory, int threshold) {
        List<File> backups = FileUtils.listFiles(directory, true, AUTOMATIC_BACKUP_REGEX);

        for (int i = backups.size(); i > threshold; i--) {
            File oldestBackup = FileUtils.getOldestFile(backups);

            Log.i(TAG, String.format("Deleting Backup: %s", oldestBackup.getName()));
            oldestBackup.delete();
        }
    }

    private File getDatabasePath(Context context) {
        return context.getDatabasePath(ExpensesDbHelper.DB_NAME);
    }

    private Directory getBackupDirectory(Context context) {
        return new AppInternalPreferences(context).getBackupDirectory();
    }

    private String createBackupFileName(@Nullable String backupName) {
        return String.format("%s.%s",
                backupName == null ? getDefaultBackupName() : backupName,
                mBackupFileExtension
        );
    }

    private String getDefaultBackupName() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().getTime()) + "_Backup";
    }
}
