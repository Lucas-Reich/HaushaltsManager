package com.example.lucas.haushaltsmanager;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Calendar;

public class BackupService extends IntentService {

    //TODO erstelle Backup service

    ExpensesDataSource mDatabase;
    SharedPreferences mPreferences;
    String mPath;
    Calendar mCalendar = Calendar.getInstance();
    String mFileName = "Backup_";

    public BackupService() {
        super("BackupService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
    //Service der automatisch jeden tag einmal läuft und die Daten des Users in einem CSV file erstellt wird
    // das automatische Backup wird in einem Standartverzeichniss abgelegt und als name wird "Backup_ZEITSTEMPEL-MIT-MILLISEKUNDEN" genommen

    //Der Service kann aber auch manuell vom User getriggert werden um ein Backup zu erstellen
    // wird der Service manuell getriggert kann auch der Dateipfad und der name des Backups mit angegeben werden
}
