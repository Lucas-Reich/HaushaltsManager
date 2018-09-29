package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Directory;

import java.io.File;

public class AppInternalPreferences {
    private static final String TAG = AppInternalPreferences.class.getSimpleName();
    private static final String APP_INTERNAL_SETTINGS = "AppInternalSettings";

    private static final String BACKUP_JOB_EXECUTED = "backupJobExecuted";
    private static final String NOTIFICATION_JOB_EXECUTED = "notificationJobExecuted";
    private static final String NOTIFICATION_JOB_ID = "notificationJobId";
    private static final String BACKUP_DIRECTORY = "backupDirectory";

    private SharedPreferences mPreferences;
    private Context mContext;

    public AppInternalPreferences(Context context) {

        mContext = context;
        mPreferences = context.getSharedPreferences(APP_INTERNAL_SETTINGS, Context.MODE_PRIVATE);
    }

    public void setBackupJobExecutionStatus(boolean backupJobStatus) {

        mPreferences.edit().putBoolean(BACKUP_JOB_EXECUTED, backupJobStatus).apply();
    }

    public boolean getBackupJobExecutionStatus() {

        return mPreferences.getBoolean(BACKUP_JOB_EXECUTED, false);
    }

    public void setNotificationStatus(boolean notificationStatus) {

        mPreferences.edit().putBoolean(NOTIFICATION_JOB_EXECUTED, notificationStatus).apply();
    }

    public boolean getNotificationStatus() {

        return mPreferences.getBoolean(NOTIFICATION_JOB_EXECUTED, false);
    }

    public void setNotificationJobId(String id) {

        mPreferences.edit().putString(NOTIFICATION_JOB_ID, id).apply();
    }

    public String getNotificationJobId() {

        return mPreferences.getString(NOTIFICATION_JOB_ID, "");
    }

    public Directory getBackupDirectory() {

        return new Directory(mPreferences.getString(BACKUP_DIRECTORY, getDefaultBackupDir()));
    }

    private String getDefaultBackupDir() {
        File file = new File(mContext.getApplicationInfo().dataDir + "/Backups");

        if (!file.exists()) {
            Log.w(TAG, "Creating not existing Backup directory.");
            file.mkdir();
        }

        return file.toString();
    }

    public void setBackupDirectory(Directory dir) {
        if (!dir.exists())
            dir.mkdir();

        mPreferences.edit().putString(BACKUP_DIRECTORY, dir.toString()).apply();
    }
}
