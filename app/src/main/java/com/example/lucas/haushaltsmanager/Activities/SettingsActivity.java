package com.example.lucas.haushaltsmanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Time;
import com.example.lucas.haushaltsmanager.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.WeekdayUtils;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Maximale Anzahl von gleichzeitig existierenden Backups.
     * Die Höhe dieser Zahl hat keinen Grund und könnte genauso gut 1 oder 100 sein.
     */
    public static final int DEFAULT_BACKUP_CAP = 20;
    public static final String DEFAULT_WEEKDAY = WeekdayUtils.MONDAY;
    public static final boolean DEFAULT_BACKUP_STATUS = true;
    public static final boolean DEFAULT_REMINDER_STATUS = false;
    public static final Time DEFAULT_REMINDER_TIME = new Time(10, 0);

    private LinearLayout firstDayLayout, createBkpLayout, concurrentBackupsLayout, currencyLayout, notificationsAllowLayout, notificationTimeLayout;
    private Button resetSettingsBtn;
    private CheckBox createBackupsChk, allowReminderChk;
    private TextView currencyNameTxt, firstDayOfWeekTxt, maxBackupCountTxt, backupCountTextTxt, notificationTimeTxt, notificationTimeTextTxt;
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

        mUserSettings = new UserSettingsPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setFirstDayOfWeek(WeekdayUtils.getWeekday(mUserSettings.getFirstDayOfWeek()));
        firstDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SingleChoiceDialog<String> weekdayPicker = new SingleChoiceDialog<>();
                weekdayPicker.createBuilder(SettingsActivity.this);
                weekdayPicker.setTitle(getString(R.string.choose_weekday));
                weekdayPicker.setContent(Arrays.asList(WeekdayUtils.getWeekdays()), -1);
                weekdayPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object weekday) {

                        setFirstDayOfWeek((String) weekday);
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                weekdayPicker.show(getFragmentManager(), "settings_choose_weekday");
            }
        });

        setAutomaticBackupStatus(mUserSettings.getAutomaticBackupStatus());
        createBkpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAutomaticBackupStatus(!createBackupsChk.isChecked());
            }
        });

        setMaxBackupCount(mUserSettings.getMaxBackupCount());
        concurrentBackupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SingleChoiceDialog<String> concurrentBackupCount = new SingleChoiceDialog<>();
                concurrentBackupCount.createBuilder(SettingsActivity.this);
                concurrentBackupCount.setTitle(getString(R.string.choose_backup_amount));
                concurrentBackupCount.setContent(Arrays.asList(getConcurrentBackupCountOptions()), -1);
                concurrentBackupCount.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object count) {

                        setMaxBackupCount(Integer.parseInt((String) count));
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                concurrentBackupCount.show(getFragmentManager(), "settings_concurrent_backups");
            }
        });

        setMainCurrency(mUserSettings.getMainCurrency());
        currencyLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SingleChoiceDialog<Currency> currencyPicker = new SingleChoiceDialog<>();
                currencyPicker.createBuilder(SettingsActivity.this);
                currencyPicker.setTitle(getString(R.string.select_currency));
                currencyPicker.setContent(CurrencyRepository.getAll(), -1);
                currencyPicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object entry) {

                        setMainCurrency((Currency) entry);
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                currencyPicker.show(getFragmentManager(), "settings_main_currency");
            }
        });

        setReminderStatus(mUserSettings.getReminderStatus());
        notificationsAllowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setReminderStatus(!allowReminderChk.isChecked());
            }
        });

        setReminderTime(mUserSettings.getReminderTime());
        notificationTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SingleChoiceDialog<String> timePicker = new SingleChoiceDialog<>();
                timePicker.createBuilder(SettingsActivity.this);
                timePicker.setTitle(getString(R.string.choose_time));
                timePicker.setContent(Arrays.asList(getTimeArray()), -1);
                timePicker.setOnEntrySelectedListener(new SingleChoiceDialog.OnEntrySelected() {
                    @Override
                    public void onPositiveClick(Object time) {

                        setReminderTime(Time.fromString((String) time));
                    }

                    @Override
                    public void onNeutralClick() {

                        //do nothing
                    }
                });
                timePicker.show(getFragmentManager(), "settings_choose_notification_time");
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

                            setFirstDayOfWeek(DEFAULT_WEEKDAY);
                            setAutomaticBackupStatus(DEFAULT_BACKUP_STATUS);
                            setMaxBackupCount(DEFAULT_BACKUP_CAP);
                            setReminderStatus(DEFAULT_REMINDER_STATUS);
                            setReminderTime(DEFAULT_REMINDER_TIME);
                            //die Hauptwährung auch zurücksetzen?
                        }
                    }
                });

                confirmationDialog.show(getFragmentManager(), "settings_confirm_reset");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Methode um ein String Array mit Zahlen zwischen 1 und DEFAULT_BACKUP_CAP zu füllen
     *
     * @return Stringarray
     */
    private String[] getConcurrentBackupCountOptions() {
        String[] options = new String[DEFAULT_BACKUP_CAP];

        for (int i = 0; i < DEFAULT_BACKUP_CAP; i++) {
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
    private void setReminderTime(Time time) {

        mUserSettings.setReminderTime(time);
        notificationTimeTxt.setText(time.getTime());
    }
}
