package com.example.lucas.haushaltsmanager.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.FileDuplicator;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.UserSettingsPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupCreatorService extends Service {
    private static final String TAG = BackupCreatorService.class.getSimpleName();

    public static final String BACKUP_NAME = "backup_name";
    public static final String BACKUP_DIR_NAME = "backup_directory";
    public static final String USER_TRIGGERED = "user_triggered";

    //.SavedDataFile
    private static final String mFileExtension = ".sdf";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isFileCopied = copyFile(
                getDatabasePath(ExpensesDbHelper.DB_NAME),
                getBackupDirectory(intent),
                getFileName(intent)
        );

//        if (isFileCopied)
//            deleteBackup(getBackupDirectory(intent));

        if (isUserTriggered(intent))
            showMessage(isFileCopied);

        return Service.START_NOT_STICKY;
    }

    /**
     * Methode um dem User eine Nachricht anzuzeigen, ob sein Aktion erfoglreich war oder nciht.
     *
     * @param isSuccessful Boolscher Wert ob die Aktion erfolgreich war oder nicht
     */
    private void showMessage(boolean isSuccessful) {
        Toast.makeText(
                this,
                isSuccessful ? R.string.created_backup : R.string.could_not_create_backup,
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Methode um den Namen des Backups zu ermitteln.
     * Ist keine individualisierter Name im Intent enthalten, dann wird einer, basierend auf dem aktuellen, Datumerstellt.
     *
     * @param intent Intent, welcher Informationen über den Dateinamen enthält.
     * @return Dateiname des Backups
     */
    private String getFileName(Intent intent) {
        if (intent != null && intent.hasExtra(BACKUP_NAME))
            return intent.getStringExtra(BACKUP_NAME) + mFileExtension;
        else
            return new SimpleDateFormat("YYYYMMdd", Locale.US).format(Calendar.getInstance().getTime()) + "_Backup" + mFileExtension;
    }

    /**
     * Methode um das Backupverzeichnis zu ermitteln.
     * Ist keine individualisiertes Verzeichnis angegeben, dann wird das standart Backupverzeichnis genommen.
     *
     * @param intent Intent, welcher Informationen über das Backupverzeichnis enthält
     * @return Backupverzeichnis
     */
    private Directory getBackupDirectory(Intent intent) {
        AppInternalPreferences preferences = new AppInternalPreferences(getApplicationContext());

        if (intent != null && intent.hasExtra(BACKUP_DIR_NAME))
            return intent.getParcelableExtra(BACKUP_DIR_NAME);
        else
            return preferences.getBackupDirectory();
    }

    /**
     * Methode um zu ermitteln, ob die Backuperstellung manuell oder automatisch ausgelöst wurde.
     *
     * @param intent Intent, welcher Informationen über den auslöser enthält
     * @return TRUE wenn der Service durch den User ausgelöst wurde, FALSE wenn nicht
     */
    private boolean isUserTriggered(Intent intent) {
        return intent != null && intent.hasExtra(USER_TRIGGERED) && intent.getBooleanExtra(USER_TRIGGERED, false);
    }

    private boolean copyFile(File sourceFile, Directory targetDir, String fileCopyName) {
        FileDuplicator fileDuplicator = new FileDuplicator(sourceFile, targetDir);

        return fileDuplicator.copy(fileCopyName);
    }

    /**
     * Methode um die ältesten Backups zu löschen, wenn die Anzahl der Backups die Mmaximal zulässige Anzahl (vom User eingestellt) überschreitet.
     */
    private void deleteBackup(Directory directory) {
        UserSettingsPreferences userSettings = new UserSettingsPreferences(getApplication());

        List<File> backups = getBackupsInDirectory(directory);
        for (int i = userSettings.getMaxBackupCount(); i < backups.size(); i++) {
            backups.get(i).delete();
        }
    }

    /**
     * Methode um alle Backupdatein in einem Verzeichniss zu bekommen.
     *
     * @return Liste der Backupdatein
     */
    private List<File> getBackupsInDirectory(Directory directory) {
        List<File> backups = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(mFileExtension))
                backups.add(file);
        }

        return backups;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
