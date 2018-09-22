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
    private Context mContext;
    private AccountRepository mAccountRepo;
    private CurrencyRepository mCurrencyRepo;

    public UserSettingsPreferences(Context context) {

        mPreferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        mContext = context;
        mAccountRepo = new AccountRepository(context);
        mCurrencyRepo = new CurrencyRepository(context);
    }

    public Currency getMainCurrency() {
        long mainCurrencyId = mPreferences.getLong(MAIN_CURRENCY_ID, -1);

        return fetchCurrency(mainCurrencyId);
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

        return mPreferences.getInt(MAX_BACKUP_COUNT, SettingsActivity.DEFAULT_BACKUP_CAP);
    }

    public void setMaxBackupCount(int maxBackupCount) {

        mPreferences.edit().putInt(MAX_BACKUP_COUNT, maxBackupCount).apply();
    }

    public boolean getReminderStatus() {

        return mPreferences.getBoolean(SEND_REMINDER_NOTIFICATIONS, SettingsActivity.DEFAULT_REMINDER_STATUS);
    }

    public void setReminderStatus(boolean reminderStatus) {

        mPreferences.edit().putBoolean(SEND_REMINDER_NOTIFICATIONS, reminderStatus).apply();
    }

    public int getFirstDayOfWeek() {

        return mPreferences.getInt(FIRST_DAY_OF_WEEK, SettingsActivity.DEFAULT_WEEKDAY);
    }

    public void setFirstDayOfWeek(String firstDayOfWeek) {
        WeekdayUtils weekdayUtils = new WeekdayUtils(mContext);

        mPreferences.edit().putInt(FIRST_DAY_OF_WEEK, weekdayUtils.getWeekdayIndex(firstDayOfWeek)).apply();
    }

    public boolean getAutomaticBackupStatus() {

        return mPreferences.getBoolean(AUTOMATIC_BACKUPS, SettingsActivity.DEFAULT_BACKUP_STATUS);
    }

    public void setAutomaticBackupStatus(boolean automaticBackupStatus) {

        mPreferences.edit().putBoolean(AUTOMATIC_BACKUPS, automaticBackupStatus).apply();
    }

    public Time getReminderTime() {

        String reminderTime = mPreferences.getString(REMINDER_TIME, SettingsActivity.DEFAULT_REMINDER_TIME.getTime());
        return Time.fromString(reminderTime);
    }

    public void setReminderTime(Time reminderTime) {

        mPreferences.edit().putString(REMINDER_TIME, reminderTime.getTime()).apply();
    }

    public boolean getNotificationStatus() {

        return mPreferences.getBoolean(ALLOW_NOTIFICATIONS, SettingsActivity.DEFAULT_REMINDER_STATUS);
    }

    public void setNotificationStatus(boolean notificationStatus) {

        mPreferences.edit().putBoolean(ALLOW_NOTIFICATIONS, notificationStatus).apply();
    }

    public Account getActiveAccount() {

        long accountId = mPreferences.getLong(ACTIVE_ACCOUNT, -1);

        return fetchAccount(accountId);
    }

    public void setActiveAccount(Account account) {

        mPreferences.edit().putLong(ACTIVE_ACCOUNT, account.getIndex()).apply();
    }

    private Currency fetchCurrency(long index) {
        try {

            return mCurrencyRepo.get(index);
        } catch (CurrencyNotFoundException e) {

            //todo sollte eigentlich nicht passieren können?
            return null;
        }
    }

    private Account fetchAccount(long index) {
        try {

            return mAccountRepo.get(index);
        } catch (AccountNotFoundException e) {

            //todo sollte eigentlich nicht passieren
            return null;
        }
    }
}
