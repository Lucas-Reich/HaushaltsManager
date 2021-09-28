package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DataImporterBackupHandler;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferencesInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;

public class Saver implements ISaver {
    private final AccountDAO accountRepository;
    private final CategoryDAO categoryRepository;
    private final ExpenseRepository bookingRepository;
    private final ActiveAccountsPreferencesInterface accountsPreferences;

    private final DataImporterBackupHandler backupHandler;

    Saver(
            AccountDAO accountRepository,
            CategoryDAO categoryRepository,
            ExpenseRepository expenseRepository,
            ActiveAccountsPreferencesInterface accountsPreferences,
            DataImporterBackupHandler backupHandler
    ) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.bookingRepository = expenseRepository;

        this.accountsPreferences = accountsPreferences;

        this.backupHandler = backupHandler;
        this.backupHandler.backup();
    }

    public static Saver create(Context context) {
        AccountDAO accountRepo = AppDatabase.getDatabase(context).accountDAO();

        CategoryDAO categoryRepo = AppDatabase.getDatabase(context).categoryDAO();

        return new Saver(
                new CachedInsertAccountRepositoryDecorator(accountRepo),
                new CachedInsertCategoryRepositoryDecorator(categoryRepo),
                new ExpenseRepository(context),
                new AddAndSetDefaultDecorator(new ActiveAccountsPreferences(context), context),
                new DataImporterBackupHandler(context, new FileBackupHandler())
        );
    }

    @Override
    public void revert() {
        backupHandler.restore();
    }

    @Override
    public void finish() {
        backupHandler.remove();
    }

    public void persist(Booking booking, Account account) {
        booking.setAccount(account);
        saveAccount(account);

        categoryRepository.insert(booking.getCategory());

        bookingRepository.insert(booking);
    }

    private void saveAccount(Account account) {
        accountRepository.insert(account);

        accountsPreferences.addAccount(account);
    }
}
