package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExpenseFilterTest {
    private ExpenseFilter mExpenseFilter;

    @Before
    public void setup() {
        mExpenseFilter = new ExpenseFilter();
    }

    @After
    public void teardown() {
        mExpenseFilter = null;
    }

    @Test
    public void testFilterByExpenditureWithNoMatchingExpenses() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditure() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(true));
        expenses.add(getSimpleExpense(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, false);

        assertEquals(4, filteredExpenses.size());
    }

    @Test
    public void testFilterByIncome() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(true));
        expenses.add(getSimpleExpense(false));
        expenses.add(getSimpleExpense(true));
        expenses.add(getSimpleExpense(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(2, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditureShouldNotIncludeParents() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parent = getSimpleExpense(true);
        parent.addChild(getSimpleExpense(false));
        expenses.add(parent);

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    private ExpenseObject getSimpleExpense(boolean isExpenditure) {
        return new ExpenseObject(
                32,
                "Ausgabe",
                12,
                mock(Calendar.class),
                isExpenditure,
                mock(Category.class),
                "",
                -1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mock(Currency.class)
        );
    }
}
