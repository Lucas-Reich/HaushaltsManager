package com.example.lucas.haushaltsmanager.Worker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.BackupHandler;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import androidx.work.WorkerParameters;

public class BackupWorker extends AbstractWorker {
    public static final String WORKER_ID = "backupWorker";

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!isEnabled())
            return Result.SUCCESS;

        BackupHandler backupHandler = new BackupHandler();
        boolean successful = backupHandler.createBackup(null, null, getApplicationContext());
        backupHandler.deleteBackupsAboveThreshold(getBackupDir(), getBackupThreshold());

        scheduleNewWorker();

        return successful ? Result.SUCCESS : Result.FAILURE;
    }

    @Override
    void saveWorkerId(String workerId) {
        new AppInternalPreferences(getApplicationContext()).setBackupJobId(workerId);
    }

    private Directory getBackupDir() {
        return new AppInternalPreferences(getApplicationContext()).getBackupDirectory();
    }

    private int getBackupThreshold() {
        return new UserSettingsPreferences(getApplicationContext()).getMaxBackupCount();
    }

    private boolean isEnabled() {
        return new UserSettingsPreferences(getApplicationContext()).getAutomaticBackupStatus();
    }
}
