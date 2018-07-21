package com.example.lucas.haushaltsmanager.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupCreatorService extends Service {
    private static final String TAG = BackupCreatorService.class.getSimpleName();

    // todo Wenn die App nicht mehr Haushaltsmanager heißen sollte dann muss hier der Name des externen Verzeichnisses angepasst werden.
    public static final String APP_EXTERNAL_STORAGE_DIR = "/Haushaltsmanager";
    public static final String BACKUP_DIR = "/Backups";

    private File mDatabaseFile;
    private File mBackupDirectory;
    private String mFileName;

    /**
     * Variable die true ist, wenn der BackupService durch den User ausgelöst wurde
     */
    boolean mUserTriggered;

    //.SavedDataFile
    final String mFileExtension = ".sdf";

    public BackupCreatorService() {

        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //todo falls der Name der Datenbank geändert werden sollte muss das hier auch angepasst werden
        mDatabaseFile = getDatabasePath("expenses.db");

        mBackupDirectory = getBackupDirectory();

        mFileName = new SimpleDateFormat("YYYYMMdd", Locale.US).format(Calendar.getInstance().getTime()) + "_Backup";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            if (intent.hasExtra("backup_directory"))
                mBackupDirectory = new File(intent.getStringExtra("backup_directory"));

            if (intent.hasExtra("backup_name"))
                mFileName = intent.getStringExtra("backup_name");

            if (intent.hasExtra("user_triggered"))
                mUserTriggered = intent.getBooleanExtra("user_triggered", false);
        }

        try {

            boolean success = copyFile(mDatabaseFile, new File(mBackupDirectory.toString() + "/" + mFileName + mFileExtension));
            if (mUserTriggered) {
                if (success) {
                    Toast.makeText(this, R.string.created_backup, Toast.LENGTH_SHORT).show();
//                    deleteBackup(); todo funktionert irgendwie nicht wie gewollt
                } else {
                    Toast.makeText(this, R.string.could_not_create_backup, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {

            Log.e(TAG, "onHandleIntent: ", e);
        } finally {

            stopSelf();
        }

        return Service.START_NOT_STICKY;
    }

    /**
     * Antwort von: https://stackoverflow.com/a/9293885
     * Methode um eine Datei zu kopieren
     *
     * @param file    Datei die kopiert werden soll
     * @param destDir Ort zu dem die Date kopiert werden soll
     * @throws IOException Exception wenn beim kopieren etwas schiefgeht
     */
    public static boolean copyFile(File file, File destDir) throws IOException {
        boolean copied = false;

        InputStream in = new FileInputStream(file);
        try {

            OutputStream out = new FileOutputStream(destDir);
            try {

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {

                    out.write(buf, 0, len);
                }

                copied = true;
            } finally {

                out.close();
            }
        } finally {

            in.close();
        }

        return copied;
    }

    /**
     * Methode um die ältesten Backups zu löschen, wenn die Anzahl der Backups die Mmaximal zulässige Anzahl (vom User eingestellt) überschreitet.
     */
    private void deleteBackup() {
        int maxBackupCount = getSharedPreferences("UserSettings", Context.MODE_PRIVATE).getInt("maxBackupCount", 20);

        List<File> backups = getBackupsInDirectory(getBackupDirectory());
        for (int i = maxBackupCount; i < backups.size(); i++) {
            backups.get(i).delete();
        }
    }

    /**
     * Methode um alle Backupdatein in einem Verzeichniss zu bekommen.
     *
     * @return Liste der Backupdatein
     */
    private List<File> getBackupsInDirectory(File directory) {
        List<File> backups = new ArrayList<>();

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().contains(mFileExtension)) {
                backups.add(file);
            }
        }

        return backups;
    }

    /**
     * Methode um den Pfad zum Speichertort der Backups zu bekommen.
     *
     * @return Speicherort des Backups
     */
    public static File getBackupDirectory() {
        createBackupDirIfNotExists();
        return new File(Environment.getExternalStorageDirectory() + APP_EXTERNAL_STORAGE_DIR + BACKUP_DIR);
    }

    /**
     * Methode um den Speicheort der Backups zu initialisieren.
     */
    private static void createBackupDirIfNotExists() {
        File externalStorageDir = new File(Environment.getExternalStorageDirectory().toString() + APP_EXTERNAL_STORAGE_DIR);
        if (!externalStorageDir.exists())
            externalStorageDir.mkdir();

        File backupStorageDir = new File(externalStorageDir.toString() + BACKUP_DIR);
        if (!backupStorageDir.exists())
            backupStorageDir.mkdir();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
