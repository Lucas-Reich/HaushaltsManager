package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockDataCreator {

    private String TAG = MockDataCreator.class.getSimpleName();

    private ExpensesDataSource mDatabase;
    private int accountCount, bookingCount, categoryCount, tagCount;
    private List<Account> mAccounts;
    private List<Category> mCategories;
    private Currency mMainCurrency;

    public MockDataCreator(Context context) {

        mDatabase = new ExpensesDataSource(context);
        mDatabase.open();

        mMainCurrency = createCurrency();
        mAccounts = createAccounts(3);
        mCategories = createCategories(3);
        createTags(3);
        createBookings(20);
    }

    private List<Account> createAccounts(int count) {

        List<Account> accounts = new ArrayList<>();

        String baseAccountName = "Account_";
        Random mRnd = new Random();

        Log.d(TAG, "createAccounts: Creating new Accounts");
        int counter = 0;
        for (; counter < count; counter++) {

            Account account = new Account(baseAccountName + counter, mRnd.nextInt(50000), mMainCurrency);
            account = mDatabase.createAccount(account);
            Log.d(TAG, "createAccounts: " + account.toString());
            accounts.add(account);
        }
        Log.d(TAG, "createAccounts: Created " + counter + " accounts");
        accountCount = counter;

        return accounts;
    }

    private Currency createCurrency() {

        return mDatabase.createCurrency(new Currency(-1, "Euro", "EUR", "€"));
    }

    private List<Category> createCategories(int count) {

        List<Category> categories = new ArrayList<>();

        String baseCategoryName = "Category_";
        Random mRnd = new Random();

        Log.d(TAG, "createCategories: started creating Categories");
        int counter = 0;
        for (; counter < count; counter++) {

            String categoryColor = "#" + String.format("%06d", mRnd.nextInt(999999));

            Category category = new Category(baseCategoryName + counter, categoryColor, false);
            category = mDatabase.createCategory(category);
            Log.d(TAG, "createCategories: " + category.toString());
            categories.add(category);
        }
        Log.d(TAG, "createCategories: created " + counter + " categories");
        categoryCount = counter;

        return categories;
    }

    private void createTags(int count) {

        String baseTagName = "Tag_";

        Log.d(TAG, "createTags: Started creating new Tags");
        int counter = 0;
        for (; counter < count; counter++) {

            Tag tag = new Tag(baseTagName + counter);
            tag = mDatabase.createTag(tag);
            Log.d(TAG, "createTags: " + tag.toString());
        }
        Log.d(TAG, "createTags: Created " + counter + " new Tags");
        tagCount = counter;
    }

    private void createBookings(int count) {

        String baseExpenseName = "Expense_";
        Random mRnd = new Random();

        Log.d(TAG, "createBookings: Started creating bookings");
        int counter = 0;
        for (; counter < count; counter++) {

            ExpenseObject expense = new ExpenseObject(baseExpenseName + counter, 10d, mRnd.nextBoolean(),mCategories.get(mRnd.nextInt(categoryCount - 1)) , mMainCurrency, mAccounts.get(mRnd.nextInt(accountCount - 1)));

            if (mRnd.nextInt(7) == 5)
                expense.addChildren(createChildBookings(3));

            mDatabase.createBooking(expense);
            Log.d(TAG, "createBookings: " + expense.toString());
        }
        Log.d(TAG, "createBookings: Created " + counter + " Bookings");
        bookingCount = counter;
    }

    private List<ExpenseObject> createChildBookings(int count) {

        List<ExpenseObject> children = new ArrayList<>();
        String baseChildExpenseName = "ChildExpense_";
        Random mRnd = new Random();

        Log.d(TAG, "createChildBookings: creating child expenses");
        for (int i = 0; i < count; i++) {

            ExpenseObject expense = new ExpenseObject(baseChildExpenseName + i, 10d, mRnd.nextBoolean(),mCategories.get(mRnd.nextInt(categoryCount - 1)) , mMainCurrency, mAccounts.get(mRnd.nextInt(accountCount - 1)));
            children.add(expense);
        }
        return children;
    }
}
