package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout languageLayout, firstDayLayout, createBkpLayout, backupFrequencyLayout, concurrentBackupsLayout, currencyLayout, notificationsAllowLayout, notificationTimeLayout;
    private Button resetSettingsBtn;
    private SharedPreferences preferences;
    private CheckBox createBackupsChk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
        languageLayout = findViewById(R.id.settings_language_wrapper); //todo die sprache sollte nicht mehr vom user änderbar sein, da er auch einfach die sprache des smartphones ändern kann
        firstDayLayout = findViewById(R.id.settings_first_day_wrapper);
        createBkpLayout = findViewById(R.id.settings_backups_enable_wrapper);
        backupFrequencyLayout = findViewById(R.id.settings_backup_frequency_wrapper);
        concurrentBackupsLayout = findViewById(R.id.settings_backups_concurrent_wrapper);
        currencyLayout = findViewById(R.id.settings_currency_wrapper);
        notificationsAllowLayout = findViewById(R.id.settings_notifications_allow_wrapper);
        notificationTimeLayout = findViewById(R.id.settings_notifications_time_wrapper);

        createBackupsChk = findViewById(R.id.settings_backups_enable_chk);

        resetSettingsBtn = findViewById(R.id.settings_reset_btn);

        preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firstDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo zeige einen AlertDialog an in dem man die Zahlen zwischen den Wochentagen auswählen kann
                int day = 0;//todo rückgabewert vom AlertDialog
                setFirstDayOfWeek(day);
            }
        });

        createBkpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAutomaticBackupStatus(!createBackupsChk.isChecked());
            }
        });

        backupFrequencyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo AlertDialog anzeigen mit dem man die Häufigkeit der backuperstellung auswählen kann
                int days = 1;//todo Rückgabewet vom AlertDialog
                setBackupCreationFrequency(days);
            }
        });

        concurrentBackupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo AlertDialog anzeigen mit dem man die Anzahl der gleichzeitigen Backups aussuchen kann
                int maxBackupCount = 10;//todo rückgabewert vom AlertDialog
                setMaxBackupCount(maxBackupCount);
            }
        });

        currencyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CurrencyPicker currencyPicker = new CurrencyPicker();
                currencyPicker.show();
            }
        });
    }

    /**
     * Methode um den ersten Tag der Woche anzupassen.
     * 0 ist dabei Montag.
     *
     * @param weekday Tag, welcher der neue erste Tag des Monats sein soll
     */
    private void setFirstDayOfWeek(int weekday) {
        preferences.edit().putInt("firstDayOfWeek", weekday).apply();
    }

    /**
     * Methode um den automatischen Backupstatus anzupassen.
     * TRUE steht dabei für automatische Backups, FALSE steht für manuelle.
     *
     * @param backupAutomatically Neuer Backup status
     */
    private void setAutomaticBackupStatus(boolean backupAutomatically) {
        preferences.edit().putBoolean("automaticBackups", backupAutomatically).apply();
        createBackupsChk.setChecked(backupAutomatically);

        setBackupSettingsClickable(backupAutomatically);
    }

    private void setBackupSettingsClickable(boolean clickableStatus) {
        //todo die Layouts (backupCount, backupFrequency) ausgrauen und den Click disablend
    }

    /**
     * Methode um die Häufigkeit der Backup erstellung anzupassen.
     *
     * @param days Anzahl in Tage
     */
    private void setBackupCreationFrequency(int days) {
        preferences.edit().putInt("backupFrequency", days).apply();
    }

    /**
     * Methode um die maximale Anzahl von gleichzeit existierenden Backups zu limitieren.
     *
     * @param maxBackupCount Wie viele Backups soll es gleichzeitig geben
     */
    private void setMaxBackupCount(int maxBackupCount) {
        preferences.edit().putInt("maxBackupCount", maxBackupCount).apply();
    }

    /**
     * Methode um die Hauptwährung des Users anzupassen.
     *
     * @param mainCurrency Neue Hauptwährung
     */
    private void setMainCurrency(Currency mainCurrency) {
        preferences.edit().putLong("mainCurrencyIndex", mainCurrency.getIndex()).apply();
    }

    /**
     * Methode zum anpassen des Notification status.
     * TRUE wenn der User keine Push Benachrichtigungen mehr bekommen soll, FALSE wenn nicht.
     *
     * @param notificationsDisabled Neuer Notification status
     */
    private void disableNotifications(boolean notificationsDisabled) {
        preferences.edit().putBoolean("notificationsEnabled", notificationsDisabled).apply();
    }

    /**
     * Methode um den Status der Erinnerungs Push Notifications anzupassen.
     *
     * @param remindUser TRUE wenn der User erinnert werden soll, FALSE wenn nicht
     */
    private void setReminderStatus(boolean remindUser) {
        preferences.edit().putBoolean("sendReminderNotification", remindUser).apply();
    }

    /**
     * Methode um die Zeit anzupassen wenn der User die "Buchungen eintragen" Benachrichtigunge bekommen soll.
     *
     * @param reminderTime Zeit zu der Der User Benachrichtigt werden soll
     */
    private void setRemiderTime(Calendar reminderTime) {
        preferences.edit().putLong("notificationReminderTime", reminderTime.getTimeInMillis()).apply();
    }
}
