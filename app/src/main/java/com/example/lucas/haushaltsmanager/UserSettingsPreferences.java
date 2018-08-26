package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Activities.SettingsActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public class UserSettingsPreferences {
    private static final String USER_SETTINGS = "UserSettings";

    private static final String MAIN_CURRENCY_ID = "mainCurrencyIndex";
    private static final String BACKUP_FREQUENCY = "backupFrequency";
    private static final String MAX_BACKUP_COUNT = "maxBackupCount";
    private static final String SEND_REMINDER_NOTIFICATIONS = "sendReminderNotification";
    private static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
    private static final String AUTOMATIC_BACKUPS = "automaticBackups";
    private static final String REMINDER_TIME = "reminderTime";
    private static final String ALLOW_NOTIFICATIONS = "allowNotifications";
    private static final String ACTIVE_ACCOUNT = "activeAccount";

    private SharedPreferences mPreferences;

    public UserSettingsPreferences(Context context) {

        mPreferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
    }

    public Currency getMainCurrency() {
        long mainCurrencyId = mPreferences.getLong(MAIN_CURRENCY_ID, -1);

        try {

            return CurrencyRepository.get(mainCurrencyId);
        } catch (CurrencyNotFoundException e) {

            //todo sollte eigentlich nicht passieren können?
            return null;
        }
    }

    public void setMainCurrency(Currency mainCurrency) {

        mPreferences.edit().putLong(MAIN_CURRENCY_ID, mainCurrency.getIndex()).apply();
    }

    public int getBackupFrequency() {

        return mPreferences.getInt(BACKUP_FREQUENCY, 1);
    }

    public void setBackupFrequency(int backupFrequency) {

        mPreferences.edit().putInt(BACKUP_FREQUENCY, backupFrequency).apply();
    }

    public int getMaxBackupCount() {

        return mPreferences.getInt(MAX_BACKUP_COUNT, SettingsActivity.MAX_BACKUP_COUNT);
    }

    public void setMaxBackupCount(int maxBackupCount) {

        mPreferences.edit().putInt(MAX_BACKUP_COUNT, maxBackupCount).apply();
    }

    public boolean getReminderStatus() {

        return mPreferences.getBoolean(SEND_REMINDER_NOTIFICATIONS, false);
    }

    public void setReminderStatus(boolean reminderStatus) {

        mPreferences.edit().putBoolean(SEND_REMINDER_NOTIFICATIONS, reminderStatus).apply();
    }

    public int getFirstDayOfWeek() {

        return mPreferences.getInt(FIRST_DAY_OF_WEEK, WeekdayUtils.WEEKDAYS.MONDAY.ordinal());
    }

    public void setFirstDayOfWeek(String firstDayOfWeek) {

        mPreferences.edit().putInt(FIRST_DAY_OF_WEEK, WeekdayUtils.getWeekday(firstDayOfWeek)).apply();
    }

    public boolean getAutomaticBackupStatus() {

        return mPreferences.getBoolean(AUTOMATIC_BACKUPS, true);
    }

    public void setAutomaticBackupStatus(boolean automaticBackupStatus) {

        mPreferences.edit().putBoolean(AUTOMATIC_BACKUPS, automaticBackupStatus).apply();
    }

    public long getReminderTime() {

        return mPreferences.getLong(REMINDER_TIME, 1L);//todo default wert sollte 18:00 repräsentieren
    }

    public void setReminderTime(long reminderTime) {

        mPreferences.edit().putLong(REMINDER_TIME, reminderTime).apply();
    }

    public boolean getNotificationStatus() {

        return mPreferences.getBoolean(ALLOW_NOTIFICATIONS, false);
    }

    public void setNotificationStatus(boolean notificationStatus) {

        mPreferences.edit().putBoolean(ALLOW_NOTIFICATIONS, notificationStatus).apply();
    }

    public Account getActiveAccount() {

        long accountId = mPreferences.getLong(ACTIVE_ACCOUNT, -1);

        try {

            return AccountRepository.get(accountId);
        } catch (AccountNotFoundException e) {

            //todo sollte eigentlich nicht passieren
            return null;
        }
    }
}
