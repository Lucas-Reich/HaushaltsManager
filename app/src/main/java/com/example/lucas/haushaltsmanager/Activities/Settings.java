package com.example.lucas.haushaltsmanager.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.SingleChoiceDialog;
import com.example.lucas.haushaltsmanager.entities.Backup;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.entities.Notification;
import com.example.lucas.haushaltsmanager.entities.Time;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.WeekdayUtils;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.BackupWorker;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.NotificationWorker;
import com.example.lucas.haushaltsmanager.Worker.WorkRequestBuilder;

import java.util.Arrays;

public class Settings extends AbstractAppCompatActivity {
    /**
     * Maximale Anzahl von gleichzeitig existierenden Backups.
     */
    public static final int DEFAULT_BACKUP_CAP = 20;
    public static final int DEFAULT_WEEKDAY = 0; // Monday
    public static final boolean DEFAULT_BACKUP_STATUS = true;
    public static final boolean DEFAULT_REMINDER_STATUS = false;
    public static final Time DEFAULT_REMINDER_TIME = new Time(10, 0);

    private LinearLayout firstDayLayout, createBkpLayout, concurrentBackupsLayout, notificationTimeLayout, backupLocationLayout;
    private Button resetSettingsBtn;
    private CheckBox createBackupsChk, allowReminderChk;
    private TextView firstDayOfWeekTxt, maxBackupCountTxt, backupCountTextTxt, notificationTimeTxt, notificationTimeTextTxt, backupLocationTxt, backupLocationHeadingTxt;
    private UserSettingsPreferences mUserSettings;
    private WeekdayUtils mWeekdayUtilsKt;
    private AppInternalPreferences mInternalPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firstDayLayout = findViewById(R.id.settings_first_day_wrapper);
        createBkpLayout = findViewById(R.id.settings_backups_enable_wrapper);
        concurrentBackupsLayout = findViewById(R.id.settings_backups_concurrent_wrapper);
        notificationTimeLayout = findViewById(R.id.settings_notifications_time_wrapper);
        backupLocationLayout = findViewById(R.id.settings_backups_location_wrapper);

        firstDayOfWeekTxt = findViewById(R.id.settings_general_first_of_week);
        backupCountTextTxt = findViewById(R.id.settings_backups_concurrent_text);
        maxBackupCountTxt = findViewById(R.id.settings_backups_concurrent_count);
        notificationTimeTextTxt = findViewById(R.id.settings_notification_time_text);
        notificationTimeTxt = findViewById(R.id.settings_notifications_time);
        backupLocationHeadingTxt = findViewById(R.id.settings_backups_location_heading);
        backupLocationTxt = findViewById(R.id.settings_backups_location_location);

        createBackupsChk = findViewById(R.id.settings_backups_enable_chk);
        allowReminderChk = findViewById(R.id.settings_notifications_notificate_chk);

        resetSettingsBtn = findViewById(R.id.settings_reset_btn);

        mUserSettings = new UserSettingsPreferences(this);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWeekdayUtilsKt = new WeekdayUtils(this);

        mInternalPreferences = new AppInternalPreferences(this);

        setFirstDayOfWeek(mWeekdayUtilsKt.getWeekday(mUserSettings.getFirstDayOfWeek()));
        firstDayLayout.setOnClickListener(view -> {

            SingleChoiceDialog<String> weekdayPicker = new SingleChoiceDialog<>();
            weekdayPicker.createBuilder(Settings.this);
            weekdayPicker.setTitle(getString(R.string.choose_weekday));
            weekdayPicker.setContent(Arrays.asList(mWeekdayUtilsKt.getWeekdays()), mUserSettings.getFirstDayOfWeek());
            weekdayPicker.setOnEntrySelectedListener(weekday -> setFirstDayOfWeek((String) weekday));
            weekdayPicker.show(getFragmentManager(), "settings_choose_weekday");
        });

        setAutomaticBackupStatus(mUserSettings.getAutomaticBackupStatus());
        createBkpLayout.setOnClickListener(view -> setAutomaticBackupStatus(!createBackupsChk.isChecked()));

        setBackupLocation(mInternalPreferences.getBackupDirectory());
        backupLocationLayout.setOnClickListener(v -> {

            if (!hasFilePermission())
                requestFilePermission();

            StorageChooser storageChooser = new StorageChooser.Builder()
                    .withActivity(Settings.this)
                    .withFragmentManager(getFragmentManager())
                    .withMemoryBar(true)
                    .allowAddFolder(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .build();

            storageChooser.show();
            storageChooser.setOnSelectListener(directory -> setBackupLocation(new Directory(directory)));
        });

        setMaxBackupCount(mUserSettings.getMaxBackupCount());
        concurrentBackupsLayout.setOnClickListener(view -> {

            SingleChoiceDialog<String> concurrentBackupCount = new SingleChoiceDialog<>();
            concurrentBackupCount.createBuilder(Settings.this);
            concurrentBackupCount.setTitle(getString(R.string.choose_backup_amount));
            concurrentBackupCount.setContent(Arrays.asList(getConcurrentBackupCountOptions()), -1);
            concurrentBackupCount.setOnEntrySelectedListener(count -> setMaxBackupCount(Integer.parseInt((String) count)));
            concurrentBackupCount.show(getFragmentManager(), "settings_concurrent_backups");
        });

        setReminderStatus(mUserSettings.getReminderStatus());
        allowReminderChk.setOnClickListener(v -> setReminderStatus(allowReminderChk.isChecked()));

        setReminderTime(mUserSettings.getReminderTime());
        notificationTimeLayout.setOnClickListener(view -> {

            SingleChoiceDialog<String> timePicker = new SingleChoiceDialog<>();
            timePicker.createBuilder(Settings.this);
            timePicker.setTitle(getString(R.string.choose_time));
            timePicker.setContent(Arrays.asList(getTimeArray()), -1);
            timePicker.setOnEntrySelectedListener(time -> setReminderTime(Time.fromString((String) time)));
            timePicker.show(getFragmentManager(), "settings_choose_notification_time");
        });

        resetSettingsBtn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(ConfirmationDialog.TITLE, getString(R.string.attention));
            bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.reset_settings_alert_message));

            ConfirmationDialog confirmationDialog = new ConfirmationDialog();
            confirmationDialog.setArguments(bundle);
            confirmationDialog.setOnConfirmationListener(reset -> {
                if (reset) {

                    setFirstDayOfWeek(mWeekdayUtilsKt.getWeekday(DEFAULT_WEEKDAY));
                    setAutomaticBackupStatus(DEFAULT_BACKUP_STATUS);
                    setMaxBackupCount(DEFAULT_BACKUP_CAP);
                    setReminderStatus(DEFAULT_REMINDER_STATUS);
                    setReminderTime(DEFAULT_REMINDER_TIME);
                    //die Hauptwährung auch zurücksetzen?
                }
            });

            confirmationDialog.show(getFragmentManager(), "settings_confirm_reset");
        });
    }

    private boolean hasFilePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        int status = ContextCompat.checkSelfPermission(this, permission);

        return status == PackageManager.PERMISSION_GRANTED;
    }

    private void requestFilePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /**
     * Methode um ein String Array mit Zahlen zwischen 1 und DEFAULT_BACKUP_CAP zu füllen
     *
     * @return String array
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
     * @return String array
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
     * @param shouldBackup Neuer Backup status
     */
    private void setAutomaticBackupStatus(boolean shouldBackup) {

        mUserSettings.setAutomaticBackupStatus(shouldBackup);
        createBackupsChk.setChecked(shouldBackup);

        setBackupSettingsClickable(shouldBackup);

        if (shouldBackup)
            scheduleBackupWorker();

        if (!shouldBackup)
            stopBackupWorker();
    }

    private void scheduleBackupWorker() {
        Backup backup = new Backup(null);

        WorkRequest backupWorkRequest = WorkRequestBuilder.from(backup);
        WorkManager.getInstance(this).enqueueUniqueWork(
                BackupWorker.WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                (OneTimeWorkRequest) backupWorkRequest
        );
    }

    private void stopBackupWorker() {
        BackupWorker.cancelWorker(this);
    }

    /**
     * Methode um das standart Backupverzeichnis anzupassen.
     *
     * @param dir Neues Backupverzeichnis
     */
    private void setBackupLocation(Directory dir) {

        mInternalPreferences.setBackupDirectory(dir);
        backupLocationTxt.setText(dir.getName());
    }

    /**
     * Methode um die Sichtbarkeit der Einträge im Backup bereich anzupassen.
     *
     * @param clickable Sichtbarkeitsstaus
     */
    private void setBackupSettingsClickable(boolean clickable) {

        if (clickable) {

            backupLocationLayout.setClickable(true);
            backupLocationHeadingTxt.setTextColor(getColorRes(R.color.primary_text_color));
            backupLocationTxt.setTextColor(getColorRes(R.color.primary_text_color));

            concurrentBackupsLayout.setClickable(true);
            backupCountTextTxt.setTextColor(getColorRes(R.color.primary_text_color));
            maxBackupCountTxt.setTextColor(getColorRes(R.color.primary_text_color));
        } else {

            backupLocationLayout.setClickable(false);
            backupLocationHeadingTxt.setTextColor(getColorRes(R.color.text_color_disabled));
            backupLocationTxt.setTextColor(getColorRes(R.color.text_color_disabled));

            concurrentBackupsLayout.setClickable(false);
            backupCountTextTxt.setTextColor(getColorRes(R.color.text_color_disabled));
            maxBackupCountTxt.setTextColor(getColorRes(R.color.text_color_disabled));
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
     * Methode um den Status der Erinnerungs Push Notifications anzupassen.
     *
     * @param shouldRemindUser TRUE wenn der User erinnert werden soll, FALSE wenn nicht
     */
    private void setReminderStatus(boolean shouldRemindUser) {
        mUserSettings.setReminderStatus(shouldRemindUser);
        allowReminderChk.setChecked(shouldRemindUser);

        setNotificationStatusClickable(shouldRemindUser);

        if (shouldRemindUser)
            scheduleNotificationWorker();

        if (!shouldRemindUser)
            stopNotificationWorker();
    }

    /**
     * Methode um den NotificationWorker zu starten
     */
    private void scheduleNotificationWorker() {
        Notification notification = new Notification(
                getString(R.string.remind_notification_title),
                getString(R.string.remind_notification_body),
                R.mipmap.ic_launcher
        );

        WorkRequest notificationWorkRequest = WorkRequestBuilder.from(notification);
        WorkManager.getInstance(this).enqueueUniqueWork(
                NotificationWorker.WORKER_TAG,
                ExistingWorkPolicy.REPLACE,
                (OneTimeWorkRequest) notificationWorkRequest
        );
    }

    /**
     * Methode um den NotificationWorker zu stoppen
     **/
    private void stopNotificationWorker() {
        NotificationWorker.stopWorker(this);
    }

    /**
     * Methode um die Sichtbarkeit der Einträge im Notificationbereich anzupassen.
     *
     * @param setClickable Sind die Einträge anwählbar oder nicht
     */
    private void setNotificationStatusClickable(boolean setClickable) {

        if (setClickable) {

            notificationTimeTextTxt.setTextColor(getColorRes(R.color.primary_text_color));
            notificationTimeTxt.setTextColor(getColorRes(R.color.primary_text_color));
            notificationTimeLayout.setClickable(true);
        } else {

            notificationTimeTextTxt.setTextColor(getColorRes(R.color.text_color_disabled));
            notificationTimeTxt.setTextColor(getColorRes(R.color.text_color_disabled));
            notificationTimeLayout.setClickable(false);
        }
    }

    /**
     * Methode um die Zeit anzupassen wenn der User die "Buchungen eintragen" Benachrichtigunge bekommen soll.
     */
    private void setReminderTime(Time time) {
        mUserSettings.setReminderTime(time);
        notificationTimeTxt.setText(time.toString());

        if (NotificationWorker.isRunning()) {
            scheduleNotificationWorker();
        }
    }
}
