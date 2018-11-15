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
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditure() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, false);

        assertEquals(4, filteredExpenses.size());
    }

    @Test
    public void testFilterByIncome() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(2, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditureShouldNotIncludeParents() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parent = getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018));
        parent.addChild(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(parent);

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonth() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.FEBRUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2017)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.JANUARY, 2018);

        assertEquals(2, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonthWithNoMatchingExpenses() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.FEBRUARY, 2019)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.FEBRUARY, 2017)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getSimpleExpense(true, getSimpleCalendar(Calendar.MARCH, 2017)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.FEBRUARY, 2018);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonthShouldNotIncludeParents() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parent = getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018));
        parent.addChild(getSimpleExpense(true, getSimpleCalendar(Calendar.DECEMBER, 2018)));

        expenses.add(parent);
        expenses.add(getSimpleExpense(false, getSimpleCalendar(Calendar.JANUARY, 2018)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.JANUARY, 2018);

        assertEquals(1, filteredExpenses.size());
    }

    private ExpenseObject getSimpleExpense(boolean isExpenditure, Calendar date) {
        return new ExpenseObject(
                32,
                "Ausgabe",
                12,
                date,
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

    private Calendar getSimpleCalendar(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        return calendar;
    }
}
