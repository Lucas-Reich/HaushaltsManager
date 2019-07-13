package com.example.lucas.haushaltsmanager.MockDataGenerator;


import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class RandomExpenseGenerator {
    private List<Account> mAccounts;
    private List<Category> mCategories;
    private Currency mCurrency;

    public RandomExpenseGenerator(Context context, Currency currency) {
        mAccounts = new AccountRepository(context).getAll();
        mCategories = new CategoryRepository(context).getAll();
        mCurrency = currency;
    }

    public void createExpenses(int count, Context context) {
        ExpenseRepository expenseRepository = new ExpenseRepository(context);

        for (; count >= 0; count--) {
            expenseRepository.insert(makeExpense(
                    withRandomCategory(),
                    withRandomAccount(),
                    withRandomCurrency(),
                    withRandomDate(2018)
            ));
        }
    }

    private Category withRandomCategory() {
        int index = new Random().nextInt(mCategories.size());

        return mCategories.get(index);
    }

    private Account withRandomAccount() {
        int index = new Random().nextInt(mAccounts.size());

        return mAccounts.get(index);
    }

    private Currency withRandomCurrency() {
        return mCurrency;
    }

    private ExpenseObject makeExpense(Category category, Account account, Currency currency, Calendar date) {
        Random random = new Random();

        return new ExpenseObject(
                -1,
                String.format("%s", CalendarUtils.formatHumanReadable(date)),
                new Price(random.nextInt(500), random.nextBoolean(), currency),
                date,
                category,
                "",
                account.getIndex(),
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                currency
        );
    }

    private Calendar withRandomDate(int year) {
        Random rnd = new Random();

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_YEAR, rnd.nextInt(364) + 1);
        date.set(Calendar.YEAR, year);

        date.set(Calendar.HOUR_OF_DAY, rnd.nextInt(23));
        date.set(Calendar.MINUTE, rnd.nextInt(59));
        date.set(Calendar.SECOND, 0);

        return date;
    }

    private class RandomParentExpenseGenerator {
        // TODO: Implement
    }
}
