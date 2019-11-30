package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Directory;

import java.io.File;

public class AppInternalPreferences {
    private static final String TAG = AppInternalPreferences.class.getSimpleName();
    private static final String APP_INTERNAL_SETTINGS = "AppInternalSettings";

    private static final String BACKUP_DIRECTORY_KEY = "backupDirectory";

    private SharedPreferences mPreferences;
    private Context mContext;

    public AppInternalPreferences(Context context) {

        mContext = context;
        mPreferences = context.getSharedPreferences(APP_INTERNAL_SETTINGS, Context.MODE_PRIVATE);
    }

    public Directory getBackupDirectory() {

        return new Directory(mPreferences.getString(BACKUP_DIRECTORY_KEY, getDefaultBackupDir()));
    }

    public void setBackupDirectory(Directory dir) {
        if (!dir.exists())
            dir.mkdir();

        mPreferences.edit().putString(BACKUP_DIRECTORY_KEY, dir.toString()).apply();
    }

    private String getDefaultBackupDir() {
        File file = new File(mContext.getApplicationInfo().dataDir + "/Backups");

        if (!file.exists()) {
            Log.w(TAG, "Creating not existing Backup directory.");
            file.mkdir();
        }

        return file.toString();
    }
}
