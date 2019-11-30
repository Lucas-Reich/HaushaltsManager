package com.example.lucas.haushaltsmanager.Backup.Handler.Decorator;

import android.content.Context;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;

public class DataImporterBackupHandler {
    private File databaseBkp;
    private File activeAccountPreferencesBkp;

    private FileBackupHandler backupHandler;

    private Directory databaseDir;
    private Directory preferencesDir;

    public DataImporterBackupHandler(Context context, FileBackupHandler backupHandler) {
        this.backupHandler = backupHandler;

        databaseDir = new Directory(context.getDatabasePath(ExpensesDbHelper.DB_NAME).getParent());

        preferencesDir = new Directory(String.format(
                "data/data/%s/shared_prefs",
                context.getPackageName()
        ));
    }

    public void backup() {
        databaseBkp = backupHandler.backup(getDatabase(databaseDir), databaseDir, null);

        activeAccountPreferencesBkp = backupHandler.backup(getPreferences(preferencesDir), preferencesDir, null);
    }

    public void restore() {
        backupHandler.restore(databaseBkp, databaseDir, ExpensesDbHelper.DB_NAME);

        if (activeAccountPreferencesBkp != null) {
            backupHandler.restore(activeAccountPreferencesBkp, preferencesDir, ActiveAccountsPreferences.PREFERENCES_NAME);
        } else {
            // Preferences did not exist previously, so I have to clear all fields which might have been written to this file
            ActiveAccountsPreferences.clear(app.getContext());
        }
    }

    public void remove() {
        FileUtils.remove(databaseBkp.getName(), databaseDir);

        FileUtils.remove(activeAccountPreferencesBkp.getName(), preferencesDir);
    }

    private File getDatabase(Directory databaseDir) {
        return new File(String.format("%s/%s",
                databaseDir.getPath(),
                ExpensesDbHelper.DB_NAME
        ));
    }

    private File getPreferences(Directory preferencesDir) {
        return new File(String.format("%s/%s",
                preferencesDir.getPath(),
                ActiveAccountsPreferences.PREFERENCES_NAME
        ));
    }
}
