package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class AppInternalPreferences {
    private static final String APP_INTERNAL_SETTINGS = "AppInternalSettings";

    private static final String BACKUP_JOB_EXECUTED = "backupJobExecuted";
    private static final String NOTIFICATION_JOB_EXECUTED = "notificationJobExecuted";
    private static final String NOTIFICATION_JOB_ID = "notificationJobId";

    private SharedPreferences mPreferences;

    public AppInternalPreferences(Context context) {

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
}
