package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

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
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditure() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(true));
        expenses.add(getExpenseWithType(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, false);

        assertEquals(4, filteredExpenses.size());
    }

    @Test
    public void testFilterByIncome() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(true));
        expenses.add(getExpenseWithType(false));
        expenses.add(getExpenseWithType(true));
        expenses.add(getExpenseWithType(false));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(2, filteredExpenses.size());
    }

    @Test
    public void testFilterByExpenditureShouldNotIncludeParents() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parent = getExpenseWithType(true);
        parent.addChild(getExpenseWithType(false));
        expenses.add(parent);

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byExpenditureType(expenses, true);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonth() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.FEBRUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2017)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.JANUARY, 2018);

        assertEquals(2, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonthWithNoMatchingExpenses() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.FEBRUARY, 2019)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.FEBRUARY, 2017)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.MARCH, 2017)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.FEBRUARY, 2018);

        assertEquals(0, filteredExpenses.size());
    }

    @Test
    public void testFilterByMonthShouldNotIncludeParents() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parent = getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2018));
        parent.addChild(getExpenseWithDate(getSimpleCalendar(Calendar.DECEMBER, 2018)));

        expenses.add(parent);
        expenses.add(getExpenseWithDate(getSimpleCalendar(Calendar.JANUARY, 2018)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byMonth(expenses, Calendar.JANUARY, 2018);

        assertEquals(1, filteredExpenses.size());
    }

    @Test
    public void testFilterByAccounts() {
        List<UUID> activeAccounts = new ArrayList<>();
        activeAccounts.add(UUID.randomUUID());
        activeAccounts.add(UUID.randomUUID());

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithAccount(activeAccounts.get(0)));
        expenses.add(getExpenseWithAccount(UUID.randomUUID()));
        expenses.add(getExpenseWithAccount(activeAccounts.get(1)));
        expenses.add(getExpenseWithAccount(activeAccounts.get(1)));
        expenses.add(getExpenseWithAccount(UUID.randomUUID()));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byAccount(expenses, activeAccounts);

        assertEquals(3, filteredExpenses.size());
    }

    @Test
    public void testFilterByAccountsShouldNotConsiderParents() {
        List<UUID> activeAccounts = new ArrayList<>();
        activeAccounts.add(UUID.randomUUID());
        activeAccounts.add(UUID.randomUUID());

        List<ExpenseObject> expenses = new ArrayList<>();

        ExpenseObject parent = getExpenseWithAccount(activeAccounts.get(0));
        parent.addChild(getExpenseWithAccount(activeAccounts.get(0)));
        expenses.add(parent);

        expenses.add(getExpenseWithAccount(activeAccounts.get(1)));
        expenses.add(getExpenseWithAccount(UUID.randomUUID()));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byAccount(expenses, activeAccounts);

        assertEquals(1, filteredExpenses.size());
    }

    @Test
    public void testFilterByAccountsWithChildrenShouldConsiderChildren() {
        List<UUID> activeAccounts = new ArrayList<>();
        activeAccounts.add(UUID.randomUUID());
        activeAccounts.add(UUID.randomUUID());

        List<ExpenseObject> expenses = new ArrayList<>();

        ExpenseObject parent = getExpenseWithAccount(activeAccounts.get(0));
        parent.addChild(getExpenseWithAccount(activeAccounts.get(0)));
        expenses.add(parent);

        expenses.add(getExpenseWithAccount(UUID.randomUUID()));
        expenses.add(getExpenseWithAccount(activeAccounts.get(1)));

        List<ExpenseObject> filteredExpenses = mExpenseFilter.byAccountWithChildren(expenses, activeAccounts);

        assertEquals(2, filteredExpenses.size());
    }

    private ExpenseObject getExpenseWithAccount(UUID accountId) {
        return getExpense(true, getSimpleCalendar(Calendar.JANUARY, 2018), accountId);
    }

    private ExpenseObject getExpenseWithDate(Calendar date) {
        return getExpense(true, date, UUID.randomUUID());
    }

    private ExpenseObject getExpenseWithType(boolean isExpenditure) {
        return getExpense(isExpenditure, getSimpleCalendar(Calendar.JANUARY, 2018), UUID.randomUUID());
    }

    private ExpenseObject getExpense(boolean isExpenditure, Calendar date, UUID accountId) {
        return new ExpenseObject(
                UUID.randomUUID(),
                "Ausgabe",
                new Price(12, isExpenditure),
                date,
                mock(Category.class),
                "",
                accountId,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    private Calendar getSimpleCalendar(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        return calendar;
    }
}
