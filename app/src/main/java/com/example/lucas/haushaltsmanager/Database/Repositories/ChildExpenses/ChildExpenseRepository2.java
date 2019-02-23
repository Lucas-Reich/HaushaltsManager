package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.List;

public class ChildExpenseRepository2 implements ChildExpenseRepositoryInterface {
    private SQLiteDatabase mDatabase;

    public ChildExpenseRepository2(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
    }

    @Override
    public ExpenseObject create(ExpenseObject child, ExpenseObject parent) throws ExpenseNotFoundException {
        return null;
    }

    @Override
    public ExpenseObject get(long id) throws ChildExpenseNotFoundException {
        return null;
    }

    @Override
    public List<ExpenseObject> getAll(long parentId) {
        return null;
    }

    @Override
    public void update(ExpenseObject child) throws ChildExpenseNotFoundException {

    }

    @Override
    public void delete(ExpenseObject child) {

    }

    @Override
    public boolean exists(ExpenseObject child) {
        return false;
    }

    @Override
    public void hide(ExpenseObject child) throws ChildExpenseNotFoundException {

    }

    @Override
    public boolean isHidden(ExpenseObject child) throws ChildExpenseNotFoundException {
        return false;
    }
}
