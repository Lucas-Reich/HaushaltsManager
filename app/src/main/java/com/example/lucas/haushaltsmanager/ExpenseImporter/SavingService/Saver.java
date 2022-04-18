package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DataImporterBackupHandler;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferencesInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class Saver implements ISaver {
    private final AccountDAO accountRepository;
    private final CategoryDAO categoryRepository;
    private final BookingDAO bookingRepository;
    private final ActiveAccountsPreferencesInterface accountsPreferences;

    private final DataImporterBackupHandler backupHandler;

    Saver(
            AccountDAO accountRepository,
            CategoryDAO categoryRepository,
            BookingDAO expenseRepository,
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

        BookingDAO bookingRepo = AppDatabase.getDatabase(context).bookingDAO();

        return new Saver(
                new CachedAccountReadRepositoryDecorator(accountRepo),
                new CachedCategoryReadRepositoryDecorator(categoryRepo),
                bookingRepo,
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

    public void persist(Booking booking, Account account, Category category) {
        saveAccount(account);

        categoryRepository.insert(category);

        saveBooking(booking, account, category);
    }

    private void saveBooking(Booking booking, Account account, Category category) {
        Account accountWithCorrectId = accountRepository.getByName(account.getName());
        booking.setAccountId(accountWithCorrectId.getId());

        Category categoryWithCorrectId = categoryRepository.getByName(category.getName());
        booking.setCategoryId(categoryWithCorrectId.getId());


        bookingRepository.insert(booking);
    }

    private void saveAccount(Account account) {
        accountRepository.insert(account);

        accountsPreferences.addAccount(account);
    }
}
