package com.example.lucas.haushaltsmanager.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.FileUtils;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupCreatorService extends Service {
    private static final String TAG = BackupCreatorService.class.getSimpleName();

    public static final String AUTOMATIC_BACKUP_REGEX = "([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))_Backup.sdf";
    public static final String BACKUP_EXTENSION_REGEX = ".*.sdf";

    //Mögliche Argumente die dem BackupServiceCreatorIntent übergeben werden können
    public static final String INTENT_BACKUP_NAME = "backup_name";
    public static final String INTENT_BACKUP_DIR = "backup_directory";
    public static final String INTENT_USER_TRIGGERED = "user_triggered";
    public static final String INTENT_PENDING_INTENT = "pending_intent";

    //.SavedDataFile
    private static final String mFileExtension = ".sdf";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isFileCopied = FileUtils.copy(
                getDatabasePath(ExpensesDbHelper.DB_NAME),
                getBackupDirectory(intent),
                getFileName(intent)
        );

        if (isFileCopied && !isUserTriggered(intent))
            deleteBackup(getBackupDirectory(intent));

        if (intent.hasExtra(INTENT_PENDING_INTENT))
            notifyCaller(intent, isFileCopied);

        return Service.START_NOT_STICKY;
    }

    /**
     * Methode um den PendingIntent auszulösen, welcher dem Service übergeben wurde.
     *
     * @param intent     Intent, welcher den PendingIntent enthält
     * @param successful Boolean wert ob die Aktion erfoglreich war oder nicht
     */
    private void notifyCaller(Intent intent, boolean successful) {
        PendingIntent pendingIntent = intent.getParcelableExtra(INTENT_PENDING_INTENT);
        try {

            pendingIntent.send(successful ? 200 : 500);
        } catch (PendingIntent.CanceledException e) {

            Log.e(TAG, "Could not send PendingIntent", e);
        }
    }

    /**
     * Methode um den Namen des Backups zu ermitteln.
     * Ist keine individualisierter Name im Intent enthalten, dann wird einer, basierend auf dem aktuellen, Datumerstellt.
     *
     * @param intent Intent, welcher Informationen über den Dateinamen enthält.
     * @return Dateiname des Backups
     */
    private String getFileName(Intent intent) {
        if (intent != null && intent.hasExtra(INTENT_BACKUP_NAME))
            return (intent.getStringExtra(INTENT_BACKUP_NAME) + mFileExtension).replace(" ", "");
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

        if (intent != null && intent.hasExtra(INTENT_BACKUP_DIR))
            return intent.getParcelableExtra(INTENT_BACKUP_DIR);
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
        return intent != null && intent.hasExtra(INTENT_USER_TRIGGERED) && intent.getBooleanExtra(INTENT_USER_TRIGGERED, false);
    }

    /**
     * Methode um die ältesten Backups zu löschen, wenn die Anzahl der Backups die Mmaximal zulässige Anzahl (vom User eingestellt) überschreitet.
     */
    private void deleteBackup(Directory directory) {
        UserSettingsPreferences userSettings = new UserSettingsPreferences(getApplication());
        List<File> backups = FileUtils.listFiles(directory, true, AUTOMATIC_BACKUP_REGEX);

        for (int i = backups.size(); i > userSettings.getMaxBackupCount(); i--) {
            FileUtils.getOldestFile(backups).delete();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
