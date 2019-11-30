package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Backup.BackupUtils;
import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DatabaseBackupHandler;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Entities.Backup;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

public class BackupWorker extends AbstractRecurringWorker {
    public static final String WORKER_TAG = "backupWorker";

    public static final String TITLE = "title";

    private Backup backup;

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);

        backup = new Backup(
                getInputData().getString(TITLE)
        );
    }

    public static void cancelWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(WORKER_TAG);
    }

    @Override
    String getTag() {
        return "BackupWorker";
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean successful = createNewBackup();

        BackupUtils.deleteBackupsAboveThreshold(getBackupDir(), getBackupThreshold());

        scheduleNextWorker(backup);

        return successful ? Result.success() : Result.failure();
    }

    private Directory getBackupDir() {
        return new AppInternalPreferences(getApplicationContext()).getBackupDirectory();
    }

    private int getBackupThreshold() {
        return new UserSettingsPreferences(getApplicationContext()).getMaxBackupCount();
    }

    private boolean createNewBackup() {
        DatabaseBackupHandler backupHandler = new DatabaseBackupHandler(getApplicationContext(), new FileBackupHandler());
        return backupHandler.backup(null, backup.getTitle());
    }
}
