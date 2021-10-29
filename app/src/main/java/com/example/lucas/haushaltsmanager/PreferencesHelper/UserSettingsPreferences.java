package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Activities.Settings;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Utils.WeekdayUtils;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Time;

import java.util.UUID;

public class UserSettingsPreferences {
    private static final String USER_SETTINGS = "UserSettings";

    private static final String MAX_BACKUP_COUNT = "maxBackupCount";
    private static final String SEND_REMINDER_NOTIFICATIONS = "sendReminderNotification";
    private static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
    private static final String AUTOMATIC_BACKUPS = "automaticBackups";
    private static final String REMINDER_TIME = "reminderTime";
    private static final String ACTIVE_ACCOUNT = "activeAccount";

    private final SharedPreferences mPreferences;
    private final Context mContext;
    private final AccountDAO accountRepo;

    public UserSettingsPreferences(Context context) {

        mPreferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        mContext = context;
        accountRepo = AppDatabase.getDatabase(context).accountDAO();
    }

    public int getMaxBackupCount() {

        return mPreferences.getInt(MAX_BACKUP_COUNT, Settings.DEFAULT_BACKUP_CAP);
    }

    public void setMaxBackupCount(int maxBackupCount) {

        mPreferences.edit().putInt(MAX_BACKUP_COUNT, maxBackupCount).apply();
    }

    public boolean getReminderStatus() {

        return mPreferences.getBoolean(SEND_REMINDER_NOTIFICATIONS, Settings.DEFAULT_REMINDER_STATUS);
    }

    public void setReminderStatus(boolean reminderStatus) {

        mPreferences.edit().putBoolean(SEND_REMINDER_NOTIFICATIONS, reminderStatus).apply();
    }

    public int getFirstDayOfWeek() {

        return mPreferences.getInt(FIRST_DAY_OF_WEEK, Settings.DEFAULT_WEEKDAY);
    }

    public void setFirstDayOfWeek(String firstDayOfWeek) {
        WeekdayUtils weekdayUtils = new WeekdayUtils(mContext);

        mPreferences.edit().putInt(FIRST_DAY_OF_WEEK, weekdayUtils.getWeekdayIndex(firstDayOfWeek)).apply();
    }

    public boolean getAutomaticBackupStatus() {

        return mPreferences.getBoolean(AUTOMATIC_BACKUPS, Settings.DEFAULT_BACKUP_STATUS);
    }

    public void setAutomaticBackupStatus(boolean automaticBackupStatus) {

        mPreferences.edit().putBoolean(AUTOMATIC_BACKUPS, automaticBackupStatus).apply();
    }

    public Time getReminderTime() {
        String reminderTime = mPreferences.getString(
                REMINDER_TIME,
                Settings.DEFAULT_REMINDER_TIME.toString()
        );

        return Time.fromString(reminderTime);
    }

    public void setReminderTime(Time reminderTime) {

        mPreferences.edit().putString(REMINDER_TIME, reminderTime.toString()).apply();
    }

    public Account getActiveAccount() {
        String rawAccountId = mPreferences.getString(ACTIVE_ACCOUNT, "00000000-0000-0000-0000-000000000000");

        return accountRepo.get(UUID.fromString(rawAccountId));
    }

    public void setActiveAccount(Account account) {

        mPreferences.edit().putString(ACTIVE_ACCOUNT, account.getId().toString()).apply();
    }
}
