package com.example.lucas.haushaltsmanager.Backup;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.entities.Backup;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupUtils {
    private static final String BACKUP_EXTENSION_REGEX_NEW = String.format(".*.%s", FileBackupHandler.BACKUP_EXTENSION);
    private static final String AUTOMATIC_BACKUP_REGEX = String.format("([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))_Backup.%s", Backup.BACKUP_FILE_EXTENSION);

    public static Directory getBackupDirectory(Context context) {
        return new AppInternalPreferences(context).getBackupDirectory();
    }

    public static List<String> getBackupsInDir(Directory directory) {
        List<File> backups = FileUtils.listFiles(directory, true, BACKUP_EXTENSION_REGEX_NEW);
        List<String> backupNames = new ArrayList<>();

        for (File file : backups) {
            backupNames.add(file.getName());
        }

        return backupNames;
    }

    public static String getDefaultBackupName() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(Calendar.getInstance().getTime())
                .concat("_Backup");
    }

    public static void deleteBackupsAboveThreshold(Directory directory, int threshold) {
        List<File> backups = FileUtils.listFiles(directory, true, AUTOMATIC_BACKUP_REGEX);

        for (int i = backups.size(); i > threshold; i--) {
            FileUtils.getOldestFile(backups)
                    .delete();
        }
    }
}
