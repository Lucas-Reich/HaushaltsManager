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
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExpenseGrouperTest {
    private ExpenseGrouper expenseGrouper;

    @Before
    public void setup() {
        expenseGrouper = new ExpenseGrouper();
    }

    @After
    public void teardown() {
        expenseGrouper = null;
    }

    @Test
    public void testGroupExpenseByMonth() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2018), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(10, 1, 2018), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(31, 1, 2018), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(1, 2, 2018), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(5, 1, 2019), mock(Category.class)));

        List<ExpenseObject> sortedExpenses = expenseGrouper.groupByMonth(expenses, 1, 2018);

        assertEquals(3, sortedExpenses.size());
    }

    @Test
    public void testGroupByYear() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(getCalendarInstance(4, 6, 1970), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(11, 6, 2017), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(4, 6, 3014), mock(Category.class)));
        expenses.add(getSimpleExpense(getCalendarInstance(4, 8, 2017), mock(Category.class)));

        List<ExpenseObject> groupedExpenses = expenseGrouper.groupByYear(expenses, 2017);

        assertEquals(2, groupedExpenses.size());
    }

    @Test
    public void testGroupByCategory() {
        Category category1 = new Category(1, "Category 1", "#000000", true, new ArrayList<Category>());
        Category category2 = new Category(2, "Category 2", "#000000", false, new ArrayList<Category>());
        Category category3 = new Category(3, "Category 3", "#000000", false, new ArrayList<Category>());

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2017), category1));
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2018), category2));
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2016), category1));
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2015), category1));
        expenses.add(getSimpleExpense(getCalendarInstance(1, 1, 2018), category3));

        HashMap<Category, List<ExpenseObject>> groupedExpenses = expenseGrouper.groupByCategory(expenses);

        assertEquals(1, groupedExpenses.get(category3).size());
        assertEquals(1, groupedExpenses.get(category2).size());
        assertEquals(3, groupedExpenses.get(category1).size());
    }

    private ExpenseObject getSimpleExpense(Calendar date, Category category) {
        return new ExpenseObject(
                881,
                "Ausgabe",
                778.4,
                date,
                false,
                category,
                "",
                1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mock(Currency.class)
        );
    }

    private Calendar getCalendarInstance(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.MONTH, month - 1);
        date.set(Calendar.YEAR, year);

        return date;
    }
}
