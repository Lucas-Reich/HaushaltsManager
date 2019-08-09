package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.os.Environment;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Backup.FileBackupService;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import java.io.File;

// TODO: Kann ich diese Klasse und den BackupHandler zusammenlegen?
class BackupService {
    private File database, accountPreferences;
    private Directory databaseDir, sharedPreferencesDir;
    private FileBackupService fileBackupService;

    BackupService() {
        databaseDir = new Directory(app.getContext().getDatabasePath(ExpensesDbHelper.DB_NAME).getParent());
        database = getDatabaseFile();

        sharedPreferencesDir = new Directory(Environment.getDataDirectory().getPath() + "/data/" + app.getApplicationName() + "/shared_prefs");
        accountPreferences = getAccountsPreferencesFile();

        fileBackupService = new FileBackupService();
    }

    void createBackup() {
        backupDatabase();

        backupSharedPreferences();
    }

    void restoreBackup() {
        restoreDatabase();

        restoreSharedPreferences();
    }

    void removeBackups() {
        removeDatabaseBackup();

        removeSharedPreferencesBackup();
    }

    private void removeDatabaseBackup() {
        fileBackupService.remove(
                database,
                databaseDir
        );
    }

    private void removeSharedPreferencesBackup() {
        fileBackupService.remove(
                accountPreferences,
                sharedPreferencesDir
        );
    }

    private void backupDatabase() {
        fileBackupService.create(
                database,
                databaseDir
        );
    }

    private void backupSharedPreferences() {
        // TODO: Was mache ich, wenn es die AccountsPreferences noch nicht gibt?
        fileBackupService.create(
                accountPreferences,
                sharedPreferencesDir
        );
    }

    private void restoreDatabase() {
        fileBackupService.restore(
                database,
                databaseDir
        );
    }

    private void restoreSharedPreferences() {
        fileBackupService.restore(
                accountPreferences,
                sharedPreferencesDir
        );
    }

    private File getDatabaseFile() {
        return new File(databaseDir.getPath() + "/" + ExpensesDbHelper.DB_NAME);
    }

    private File getAccountsPreferencesFile() {
        return new File(sharedPreferencesDir.getPath() + "/" + ActiveAccountsPreferences.PREFERENCES_NAME + ".xml");
    }
}
