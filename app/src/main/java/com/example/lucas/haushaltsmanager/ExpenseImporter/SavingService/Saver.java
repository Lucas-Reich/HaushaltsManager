package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.content.Context;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DataImporterBackupHandler;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferencesInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.AddAndSetDefaultDecorator;

public class Saver implements ISaver {
    private AccountRepositoryInterface accountRepository;
    private final CategoryRepositoryInterface categoryRepository;
    private ExpenseRepository bookingRepository;
    private ActiveAccountsPreferencesInterface accountsPreferences;

    private DataImporterBackupHandler backupHandler;

    Saver(
            AccountRepositoryInterface accountRepository,
            CategoryRepositoryInterface categoryRepository,
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
        return new Saver(
                new CachedInsertAccountRepositoryDecorator(new AccountRepository(context)),
                new CachedInsertCategoryRepositoryDecorator(new CategoryRepository(context)),
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

    public void persist(ExpenseObject booking, Account account) {
        booking.setAccount(account);
        saveAccount(account);

        saveCategory(booking.getCategory());

        bookingRepository.insert(booking);
    }

    private void saveAccount(Account account) {
        try {
            accountRepository.insert(account);

            accountsPreferences.addAccount(account);
        } catch (AccountCouldNotBeCreatedException e) {
            Log.d("TEST", "Could not create account");
            // TODO: Do nothing?
        }
    }

    private void saveCategory(Category category) {
        try {
            categoryRepository.insert(category);
        } catch (CategoryCouldNotBeCreatedException e) {
            Log.d("TEST", "Could not create category");
        }
    }
}
