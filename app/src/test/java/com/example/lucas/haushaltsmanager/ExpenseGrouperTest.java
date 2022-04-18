package com.example.lucas.haushaltsmanager;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExpenseGrouperTest {
    private ExpenseGrouper mExpenseGrouper;

    @Before
    public void setup() {
        mExpenseGrouper = new ExpenseGrouper();
    }

    @After
    public void teardown() {
        mExpenseGrouper = null;
    }

    @Test
    public void testGroupExpenseByMonth() {
        List<Booking> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.FEBRUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(10, Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(31, Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(5, Calendar.JANUARY, 2019)));

        List<Booking> sortedExpenses = mExpenseGrouper.byMonth(expenses, Calendar.JANUARY, 2018);

        assertEquals(3, sortedExpenses.size());
    }

    @Test
    public void testGroupByYear() {
        List<Booking> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(4, Calendar.JUNE, 1970)));
        expenses.add(getExpenseWithDate(getSimpleDate(11, Calendar.JUNE, 2017)));
        expenses.add(getExpenseWithDate(getSimpleDate(4, Calendar.JUNE, 3014)));
        expenses.add(getExpenseWithDate(getSimpleDate(4, Calendar.JUNE, 2017)));

        List<Booking> groupedExpenses = mExpenseGrouper.byYear(expenses, 2017);

        assertEquals(2, groupedExpenses.size());
    }

    @Test
    public void testGroupByCategory() {
        Category category1 = getSimpleCategory("Kategorie 1");
        Category category2 = getSimpleCategory("Kategorie 2");
        Category category3 = getSimpleCategory("Kategorie 3");

        List<Booking> expenses = new ArrayList<>();
        expenses.add(getExpenseWithCategory(category1));
        expenses.add(getExpenseWithCategory(category2));
        expenses.add(getExpenseWithCategory(category1));
        expenses.add(getExpenseWithCategory(category1));
        expenses.add(getExpenseWithCategory(category3));

        HashMap<Category, List<Booking>> groupedExpenses = mExpenseGrouper.byCategory(expenses);

        assertEquals(1, groupedExpenses.get(category3).size());
        assertEquals(1, groupedExpenses.get(category2).size());
        assertEquals(3, groupedExpenses.get(category1).size());
    }

    @Test
    public void testGroupByMonths() {
        List<Booking> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.MARCH, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.MARCH, 2017)));
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.JUNE, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(1, Calendar.DECEMBER, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(31, Calendar.JANUARY, 2018)));
        expenses.add(getExpenseWithDate(getSimpleDate(31, Calendar.JANUARY, 2018)));

        List<List<Booking>> groupedExpenses = mExpenseGrouper.byMonths(expenses, 2018);

        assertEquals(2, groupedExpenses.get(Calendar.JANUARY).size());
        assertEquals(0, groupedExpenses.get(Calendar.FEBRUARY).size());
        assertEquals(1, groupedExpenses.get(Calendar.MARCH).size());
        assertEquals(0, groupedExpenses.get(Calendar.APRIL).size());
        assertEquals(0, groupedExpenses.get(Calendar.MAY).size());
        assertEquals(1, groupedExpenses.get(Calendar.JUNE).size());
        assertEquals(0, groupedExpenses.get(Calendar.JULY).size());
        assertEquals(0, groupedExpenses.get(Calendar.AUGUST).size());
        assertEquals(0, groupedExpenses.get(Calendar.SEPTEMBER).size());
        assertEquals(0, groupedExpenses.get(Calendar.OCTOBER).size());
        assertEquals(0, groupedExpenses.get(Calendar.NOVEMBER).size());
        assertEquals(1, groupedExpenses.get(Calendar.DECEMBER).size());
    }

    private Booking getExpenseWithDate(Calendar date) {
        return getExpense(date, getSimpleCategory("Kategorie"));
    }

    private Booking getExpenseWithCategory(Category category) {
        return getExpense(getSimpleDate(1, Calendar.JANUARY, 2018), category);
    }

    private Booking getExpense(Calendar date, Category category) {
        return new Booking(
                UUID.randomUUID(),
                "Ausgabe",
                new Price(-778.4),
                date,
                category.getId(),
                UUID.randomUUID(),
                null
        );
    }

    private Calendar getSimpleDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.YEAR, year);

        return date;
    }

    private Category getSimpleCategory(String name) {
        return new Category(
                name,
                Color.Companion.black(),
                ExpenseType.Companion.deposit()
        );
    }
}
