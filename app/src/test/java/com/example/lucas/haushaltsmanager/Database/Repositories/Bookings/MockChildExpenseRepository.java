package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.ArrayList;
import java.util.List;

class MockChildExpenseRepository implements ChildExpenseRepositoryInterface {
    @Override
    public boolean exists(ExpenseObject childExpense) {
        return false;
    }

    @Override
    public ExpenseObject addChildToBooking(ExpenseObject childExpense, ExpenseObject parentBooking) throws AddChildToChildException {
        parentBooking.addChild(childExpense);

        return parentBooking;
    }

    @Override
    public ExpenseObject combineExpenses(List<ExpenseObject> expenses) {
        return null;
    }

    @Override
    public ExpenseObject extractChildFromBooking(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        return null;
    }

    @Override
    public ExpenseObject get(long childExpenseId) throws ChildExpenseNotFoundException {
        return null;
    }

    @Override
    public List<ExpenseObject> getAll(long parentExpenseId) {
        return new ArrayList<>();
    }

    @Override
    public ExpenseObject insert(ExpenseObject parentExpense, ExpenseObject childExpense) {
        return null;
    }

    @Override
    public void update(ExpenseObject childExpense) throws ChildExpenseNotFoundException {

    }

    @Override
    public void delete(ExpenseObject childExpense) throws CannotDeleteChildExpenseException {

    }

    @Override
    public void hide(ExpenseObject childExpense) throws ChildExpenseNotFoundException {

    }

    @Override
    public void closeDatabase() {

    }
}
