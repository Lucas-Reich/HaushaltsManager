package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferencesInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AddAndSetDefaultDecorator;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class Saver implements ISaver {
    private AccountRepositoryInterface accountRepository;
    private ChildCategoryRepositoryInterface childCategoryRepository;
    private ExpenseRepository bookingRepository;
    private ActiveAccountsPreferencesInterface accountsPreferences;

    private BackupService backupService;

    private final Category parentCategory;

    Saver(
            AccountRepositoryInterface accountRepository,
            ChildCategoryRepositoryInterface childCategoryRepository,
            ExpenseRepository expenseRepository,
            ActiveAccountsPreferencesInterface accountsPreferences,
            BackupService backupService,
            Category parentCategory
    ) {
        this.accountRepository = accountRepository;
        this.childCategoryRepository = childCategoryRepository;
        this.bookingRepository = expenseRepository;

        this.accountsPreferences = accountsPreferences;

        this.parentCategory = parentCategory;

        this.backupService = backupService;
        this.backupService.createBackup();
    }

    public static Saver create(Context context) {
        CategoryRepositoryInterface categoryRepository = new CategoryRepository(context);
        Category parentCategory = categoryRepository.insert(new Category(
                context.getString(R.string.imported_categories_parent_name),
                Color.random(),
                true,
                new ArrayList<Category>()
        ));

        return new Saver(
                new CachedInsertAccountRepositoryDecorator(new AccountRepository(context)),
                new CachedInsertChildCategoryRepositoryDecorator(new ChildCategoryRepository(context)),
                new ExpenseRepository(context),
                new AddAndSetDefaultDecorator(new ActiveAccountsPreferences(context), context),
                new BackupService(),
                parentCategory
        );
    }

    @Override
    public void revert() {
        backupService.restoreBackup();
    }

    @Override
    public void finish() {
        backupService.removeBackups();
    }

    public void persist(ExpenseObject booking, Account account) {
        saveAccountAndAttachToBooking(account, booking);

        saveCategoryAndAttachToBooking(booking.getCategory(), booking);

        bookingRepository.insert(booking);
    }

    private void saveAccountAndAttachToBooking(Account account, ExpenseObject booking) {
        Account savedAccount = accountRepository.create(account);

        booking.setAccount(savedAccount);

        accountsPreferences.addAccount(savedAccount);
    }

    private void saveCategoryAndAttachToBooking(Category category, ExpenseObject booking) {
        Category savedCategory = childCategoryRepository.insert(parentCategory, category);

        booking.setCategory(savedCategory);
    }
}
