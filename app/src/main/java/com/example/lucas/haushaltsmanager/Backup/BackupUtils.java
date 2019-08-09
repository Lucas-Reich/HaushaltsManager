package com.example.lucas.haushaltsmanager.Backup;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Backup.BackupHandler;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupUtils {
    // IMPROVEMENT: Tests für die Klasse erstellen
    public static Directory getBackupDirectory(Context context) {
        return new AppInternalPreferences(context).getBackupDirectory();
    }

    public static List<String> getBackupsInDir(Directory directory) {
        List<File> backups = FileUtils.listFiles(directory, true, BackupHandler.BACKUP_EXTENSION_REGEX);
        List<String> backupNames = new ArrayList<>();
        for (File file : backups) {
            backupNames.add(file.getName());
        }

        return backupNames;
    }

    public static String getDefaultBackupName() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().getTime()).concat("_Backup");
    }
}
