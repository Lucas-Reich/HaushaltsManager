package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Activities.Settings;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Time;
import com.example.lucas.haushaltsmanager.Utils.WeekdayUtils;

public class UserSettingsPreferences {
    private static final String USER_SETTINGS = "UserSettings";

    private static final String MAIN_CURRENCY_ID = "mainCurrencyIndex";
    private static final String MAX_BACKUP_COUNT = "maxBackupCount";
    private static final String SEND_REMINDER_NOTIFICATIONS = "sendReminderNotification";
    private static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
    private static final String AUTOMATIC_BACKUPS = "automaticBackups";
    private static final String REMINDER_TIME = "reminderTime";
    private static final String ACTIVE_ACCOUNT = "activeAccount";

    private final SharedPreferences mPreferences;
    private final Context mContext;
    private final AccountRepository mAccountRepo;
    private final CurrencyRepository mCurrencyRepo;

    public UserSettingsPreferences(Context context) {

        mPreferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        mContext = context;
        mAccountRepo = new AccountRepository(context);
        mCurrencyRepo = new CurrencyRepository(context);
    }

    public Currency getMainCurrency() {
        long mainCurrencyId = mPreferences.getLong(MAIN_CURRENCY_ID, 1);

        return fetchCurrency(mainCurrencyId);
    }

    public void setMainCurrency(Currency mainCurrency) {

        mPreferences.edit().putLong(MAIN_CURRENCY_ID, mainCurrency.getIndex()).apply();
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

            // TODO: Kann dieser Fall eintreten?
            return null;
        }
    }

    private Account fetchAccount(long index) {
        try {

            return mAccountRepo.get(index);
        } catch (AccountNotFoundException e) {

            // TODO: Wenn der Benutzer noch kein Konto erstellt hat, dann wird NULL zurückgegeben.
            return null;
        }
    }
}
