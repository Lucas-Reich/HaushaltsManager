package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.CurrencyPicker;
import com.example.lucas.haushaltsmanager.Dialogs.StringSingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.WeekdayUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Maximale Anzahl von gleichzeitig existierenden Backups.
     * Die Höhe dieser Zahl hat keinen Grund und könnte genauso gut 1 oder 100 sein.
     */
    public static final int MAX_BACKUP_COUNT = 20;

    private LinearLayout firstDayLayout, createBkpLayout, concurrentBackupsLayout, currencyLayout, notificationsAllowLayout, notificationTimeLayout;
    private Button resetSettingsBtn;
    private SharedPreferences preferences;
    private CheckBox createBackupsChk, allowReminderChk;
    private TextView currencyNameTxt, firstDayOfWeekTxt, maxBackupCountTxt, backupCountTextTxt, notificationTimeTxt, notificationTimeTextTxt;
    private ImageButton mBackArrow;
    private UserSettingsPreferences mUserSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firstDayLayout = findViewById(R.id.settings_first_day_wrapper);
        createBkpLayout = findViewById(R.id.settings_backups_enable_wrapper);
        concurrentBackupsLayout = findViewById(R.id.settings_backups_concurrent_wrapper);
        currencyLayout = findViewById(R.id.settings_currency_wrapper);
        notificationsAllowLayout = findViewById(R.id.settings_notifications_allow_wrapper);
        notificationTimeLayout = findViewById(R.id.settings_notifications_time_wrapper);

        currencyNameTxt = findViewById(R.id.settings_currency_name);
        firstDayOfWeekTxt = findViewById(R.id.settings_general_first_of_week);
        backupCountTextTxt = findViewById(R.id.settings_backups_concurrent_text);
        maxBackupCountTxt = findViewById(R.id.settings_backups_concurrent_count);
        notificationTimeTextTxt = findViewById(R.id.settings_notification_time_text);
        notificationTimeTxt = findViewById(R.id.settings_notifications_time);

        createBackupsChk = findViewById(R.id.settings_backups_enable_chk);
        allowReminderChk = findViewById(R.id.settings_notifications_notificate_chk);

        resetSettingsBtn = findViewById(R.id.settings_reset_btn);

        preferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        mUserSettings = new UserSettingsPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBackArrow = findViewById(R.id.back_arrow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //todo initialien status der einzelnen checkboxen und textviews so anpassen, dass sie ihren status aus den sharedpreferences bekommen

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firstDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(StringSingleChoiceDialog.TITLE, getString(R.string.choose_weekday));
                bundle.putStringArray(StringSingleChoiceDialog.CONTENT, WeekdayUtils.getWeekdays());

                StringSingleChoiceDialog weekdayDialog = new StringSingleChoiceDialog();
                weekdayDialog.setArguments(bundle);
                weekdayDialog.setOnEntrySelectedListener(new StringSingleChoiceDialog.OnEntrySelected() {

                    @Override
                    public void onEntrySelected(String weekday) {

                        setFirstDayOfWeek(weekday);
                    }
                });

                weekdayDialog.show(getFragmentManager(), "settings_choose_weekday");
            }
        });

        createBkpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAutomaticBackupStatus(!createBackupsChk.isChecked());
            }
        });

        concurrentBackupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(StringSingleChoiceDialog.TITLE, getString(R.string.choose_backup_amount));
                bundle.putStringArray(StringSingleChoiceDialog.CONTENT, getConcurrentBackupCountOptions());

                StringSingleChoiceDialog concurrentBackupDialog = new StringSingleChoiceDialog();
                concurrentBackupDialog.setArguments(bundle);
                concurrentBackupDialog.setOnEntrySelectedListener(new StringSingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onEntrySelected(String entry) {

                        setMaxBackupCount(Integer.parseInt(entry));
                    }
                });

                concurrentBackupDialog.show(getFragmentManager(), "settings_concurrent_backups");
            }
        });

        currencyLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(CurrencyPicker.TITLE, getString(R.string.select_currency));

                CurrencyPicker currencyPicker = new CurrencyPicker();
                currencyPicker.setArguments(bundle);
                currencyPicker.setCurrencies(CurrencyRepository.getAll());
                currencyPicker.setOnCurrencySelectedListener(new CurrencyPicker.OnCurrencySelected() {
                    @Override
                    public void onCurrencySelected(Currency currency) {

                        setMainCurrency(currency);
                    }
                });
                currencyPicker.show(getFragmentManager(), "settings_main_currency");
            }
        });

        notificationsAllowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setReminderStatus(!allowReminderChk.isChecked());
            }
        });

        notificationTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(StringSingleChoiceDialog.TITLE, getString(R.string.choose_time));
                bundle.putStringArray(StringSingleChoiceDialog.CONTENT, getTimeArray());

                StringSingleChoiceDialog timePickerDialog = new StringSingleChoiceDialog();
                timePickerDialog.setArguments(bundle);
                timePickerDialog.setOnEntrySelectedListener(new StringSingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onEntrySelected(String entry) {

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);

                        try {
                            Date test = sdf.parse(entry);
                            Time time = new Time();
                            time.set(test.getTime());
                            setReminderTime(time.hour, time.minute);
                        } catch (ParseException e) {

                            //do nothing
                        }
                    }
                });

                timePickerDialog.show(getFragmentManager(), "settings_choose_notification_time");
            }
        });

        resetSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
                bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.reset_settings_alert_message));

                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
                    @Override
                    public void onConfirmationResult(boolean reset) {

                        if (reset) {

                            //todo einstellungen auf dern standart zurücksetzen
                            Toast.makeText(SettingsActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                confirmationDialog.show(getFragmentManager(), "settings_confirm_reset");
            }
        });
    }

    /**
     * Methode um ein String Array mit Zahlen zwischen 1 und MAX_BACKUP_COUNT zu füllen
     *
     * @return Stringarray
     */
    private String[] getConcurrentBackupCountOptions() {
        String[] options = new String[MAX_BACKUP_COUNT];

        for (int i = 0; i < MAX_BACKUP_COUNT; i++) {
            options[i] = Integer.toString(i + 1);
        }

        return options;
    }

    /**
     * Methode um ein Stringarray mit Uhrzeiten zu bekommen.
     *
     * @return Stringarray
     */
    private String[] getTimeArray() {
        String[] timeArray = new String[24];

        for (int i = 0; i < 24; i++) {
            String time = (i + 1) < 10 ? "0" + (i + 1) : "" + (i + 1);
            time = time.concat(":00");

            timeArray[i] = time;
        }

        return timeArray;
    }

    /**
     * Methode um den ersten Tag der Woche anzupassen.
     * 0 ist dabei Montag.
     *
     * @param weekday Tag, welcher der neue erste Tag des Monats sein soll
     */
    private void setFirstDayOfWeek(String weekday) {

        mUserSettings.setFirstDayOfWeek(weekday);
        firstDayOfWeekTxt.setText(weekday);
    }

    /**
     * Methode um den automatischen Backupstatus anzupassen.
     * TRUE steht dabei für automatische Backups, FALSE steht für manuelle.
     *
     * @param backupAutomatically Neuer Backup status
     */
    private void setAutomaticBackupStatus(boolean backupAutomatically) {

        mUserSettings.setAutomaticBackupStatus(backupAutomatically);
        createBackupsChk.setChecked(backupAutomatically);

        setBackupSettingsClickable(backupAutomatically);
    }

    /**
     * Methode um die Sichtbarkeit der Einträge im Backup bereich anzupassen.
     *
     * @param clickable Sichtbarkeitsstaus
     */
    private void setBackupSettingsClickable(boolean clickable) {

        if (clickable) {

            concurrentBackupsLayout.setClickable(true);
            backupCountTextTxt.setTextColor(getResources().getColor(R.color.primary_text_color));
            maxBackupCountTxt.setTextColor(getResources().getColor(R.color.primary_text_color));
        } else {

            concurrentBackupsLayout.setClickable(false);
            backupCountTextTxt.setTextColor(getResources().getColor(R.color.text_color_disabled));
            maxBackupCountTxt.setTextColor(getResources().getColor(R.color.text_color_disabled));
        }
    }

    /**
     * Methode um die maximale Anzahl von gleichzeit existierenden Backups zu limitieren.
     *
     * @param maxBackupCount Wie viele Backups soll es gleichzeitig geben
     */
    private void setMaxBackupCount(int maxBackupCount) {

        mUserSettings.setMaxBackupCount(maxBackupCount);
        maxBackupCountTxt.setText(String.format("%s", maxBackupCount));
    }

    /**
     * Methode um die Hauptwährung des Users anzupassen.
     *
     * @param mainCurrency Neue Hauptwährung
     */
    private void setMainCurrency(Currency mainCurrency) {

        mUserSettings.setMainCurrency(mainCurrency);
        currencyNameTxt.setText(mainCurrency.getName());
    }

    /**
     * Methode zum anpassen des Notification status.
     * TRUE wenn der User keine Push Benachrichtigungen mehr bekommen soll, FALSE wenn nicht.
     *
     * @param notificationStatus Neuer Notification status
     */
    private void disableNotifications(boolean notificationStatus) {

        mUserSettings.setNotificationStatus(notificationStatus);
    }

    /**
     * Methode um den Status der Erinnerungs Push Notifications anzupassen.
     *
     * @param remindUser TRUE wenn der User erinnert werden soll, FALSE wenn nicht
     */
    private void setReminderStatus(boolean remindUser) {

        mUserSettings.setReminderStatus(remindUser);
        allowReminderChk.setChecked(remindUser);

        setNotificationStatusClickable(remindUser);
    }

    /**
     * Methode um die Sichtbarkeit der Einträge im Notificationbereich anzupassen.
     *
     * @param clickable Sind die Einträge anwählbra oder nicht
     */
    private void setNotificationStatusClickable(boolean clickable) {

        if (clickable) {

            notificationTimeTextTxt.setTextColor(getResources().getColor(R.color.primary_text_color));
            notificationTimeTxt.setTextColor(getResources().getColor(R.color.primary_text_color));
            notificationTimeLayout.setClickable(true);
        } else {

            notificationTimeTextTxt.setTextColor(getResources().getColor(R.color.text_color_disabled));
            notificationTimeTxt.setTextColor(getResources().getColor(R.color.text_color_disabled));
            notificationTimeLayout.setClickable(false);
        }
    }

    /**
     * Methode um die Zeit anzupassen wenn der User die "Buchungen eintragen" Benachrichtigunge bekommen soll.
     */
    private void setReminderTime(int one, int two) {

        Date date = new Date();
        date.setHours(one);
        date.setMinutes(two);

//        preferences.edit().putLong("notificationReminderTime", reminderTime).apply();
        //todo wenn die stunde kleiner als 10 ist muss auch noch eine 0 vor der zahl erscheinen
        notificationTimeTxt.setText(String.format("%s:00", date.getHours()));//todo long in time convertieren
    }
}
