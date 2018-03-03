package com.example.lucas.haushaltsmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BackupService extends Service {

    final String TAG = BackupService.class.getSimpleName();

    File mDatabaseFile;
    File mBackupDirectory;
    String mFileName = "Backup_";

    //.SavedDataFile
    final String mFileExtension = ".sdf";

    public BackupService() {

        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabaseFile = getDatabasePath("expenses.db");

        mBackupDirectory = new File(getApplicationContext().getFilesDir().toString() + "/Backups");

        mFileName = new SimpleDateFormat("YYYY_dd_mm_HH_mm_ss_SS", Locale.US).format(Calendar.getInstance().getTime());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            if (intent.hasExtra("backup_directory"))
                mBackupDirectory = new File(intent.getStringExtra("backup_directory"));

            if (intent.hasExtra("backup_name"))
                mFileName = intent.getStringExtra("backup_name");
        }

        try {

            copy(mDatabaseFile, new File(mBackupDirectory.toString() + "/" + mFileName + mFileExtension));
        } catch (IOException e) {

            Log.e(TAG, "onHandleIntent: ", e);
        } finally {

            stopSelf();
        }

        return Service.START_NOT_STICKY;
    }

    /**
     * Methode um eine Datei zu kopieren
     *
     * @param src Datei die kopiert werden soll
     * @param dst Ort zu dem die Date kopiert werden soll
     * @throws IOException Exception wenn beim kopieren etwas schiefgeht
     *                     <p>
     *                     Antwort von: https://stackoverflow.com/a/9293885
     */
    public static void copy(File src, File dst) throws IOException {

        InputStream in = new FileInputStream(src);
        try {

            OutputStream out = new FileOutputStream(dst);
            try {

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {

                    out.write(buf, 0, len);
                }
            } finally {

                out.close();
            }
        } finally {

            in.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, R.string.created_backup, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
