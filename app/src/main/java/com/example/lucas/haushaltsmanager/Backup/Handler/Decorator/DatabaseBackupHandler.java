package com.example.lucas.haushaltsmanager.Backup.Handler.Decorator;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Backup.BackupUtils;
import com.example.lucas.haushaltsmanager.Backup.Exceptions.SQLiteOpenDatabaseFileException;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferencesRebuildStrategy;
import com.example.lucas.haushaltsmanager.PreferencesHelper.PreferencesRefresher;

import java.io.File;

public class DatabaseBackupHandler {
    private final Directory defaultBackupDir;
    private final Directory databaseDir;

    private final FileBackupHandler backupHandler;
    private final DatabaseFileValidator databaseFileValidator;

    private final PreferencesRefresher preferencesRefresher;

    public DatabaseBackupHandler(Context context, FileBackupHandler backupHandler) {
        databaseDir = new Directory(context.getDatabasePath(AppDatabase.DATABASE_NAME).getParent());

        defaultBackupDir = BackupUtils.getBackupDirectory(context);

        this.backupHandler = backupHandler;

        databaseFileValidator = new DatabaseFileValidator();

        preferencesRefresher = new PreferencesRefresher(new ActiveAccountsPreferences(context));
        preferencesRefresher.setRebuildStrategy(new ActiveAccountsPreferencesRebuildStrategy());
    }

    public boolean backup(@Nullable Directory targetDir, @Nullable String fileName) {
        if (null == targetDir) {
            targetDir = defaultBackupDir;
        }

        return backupHandler.backup(getDatabase(), targetDir, fileName) != null;
    }

    public boolean restore(File database) throws SQLiteOpenDatabaseFileException, InvalidFileException {
        databaseFileValidator.guardAgainstNoDatabase(database);

        databaseFileValidator.guardAgainstWrongDatabaseVersion(database);

        databaseFileValidator.guardAgainstInvalidDatabaseSchema(database);

        boolean success = backupHandler.restore(database, databaseDir, AppDatabase.DATABASE_NAME) != null;

        rebuildActiveAccountsPreferences();
        // TODO: Was passiert, wenn es den main account nicht mehr gibt?
        return success;
    }

    private File getDatabase() {
        return new File(String.format("%s/%s",
                databaseDir.getPath(),
                AppDatabase.DATABASE_NAME
        ));
    }

    private void rebuildActiveAccountsPreferences() {
        preferencesRefresher.refresh(app.getContext());
    }
}
