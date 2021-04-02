package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ExpenseSumTest {
    private ExpenseSum mExpenseSum;

    @Before
    public void setup() {
        mExpenseSum = new ExpenseSum();
    }

    @After
    public void teardown() {
        mExpenseSum = null;
    }

    @Test
    public void testSumByCategoriesWithDifferentCategories() {
        Category category1 = getSimpleCategory("Kategorie 1");
        Category category2 = getSimpleCategory("Kategorie 2");
        Category category3 = getSimpleCategory("Kategorie 3");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithCategory(category3, 113));
        expenses.add(getExpenseWithCategory(category2, 77));
        expenses.add(getExpenseWithCategory(category2, 23));
        expenses.add(getExpenseWithCategory(category3, 100));
        expenses.add(getExpenseWithCategory(category3, 9));
        expenses.add(getExpenseWithCategory(category3, 91));
        expenses.add(getExpenseWithCategory(category1, 1337));

        HashMap<Category, Double> categorySum = mExpenseSum.byCategory(expenses);

        assertEquals(1337, categorySum.get(category1), 0);
        assertEquals(100, categorySum.get(category2), 0);
        assertEquals(313, categorySum.get(category3), 0);
    }

    @Test
    public void testSumByCategoriesWithPositiveValues() {
        Category category = getSimpleCategory("Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithCategory(category, 170));
        expenses.add(getExpenseWithCategory(category, 1003));
        expenses.add(getExpenseWithCategory(category, 163));
        expenses.add(getExpenseWithCategory(category, 1));

        HashMap<Category, Double> categorySum = mExpenseSum.byCategory(expenses);

        assertEquals(1337, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoryWithNegativeValues() {
        Category category = getSimpleCategory("Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithCategory(category, -100));
        expenses.add(getExpenseWithCategory(category, -17));
        expenses.add(getExpenseWithCategory(category, -87));

        HashMap<Category, Double> categorySum = mExpenseSum.byCategory(expenses);

        assertEquals(-204, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoriesWithChildren() {
        Category category = getSimpleCategory("Kategorie");

        ExpenseObject expenseWithChildren = getExpenseWithCategory(category, 0);
        expenseWithChildren.addChild(getExpenseWithCategory(category, 1000));
        expenseWithChildren.addChild(getExpenseWithCategory(category, 1500));
        expenseWithChildren.addChild(getExpenseWithCategory(category, 174));

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(expenseWithChildren);
        expenses.add(getExpenseWithCategory(category, -1337));

        HashMap<Category, Double> categorySum = mExpenseSum.byCategory(expenses);

        assertEquals(1337, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoriesWithEmptyList() {
        List<ExpenseObject> expenses = new ArrayList<>();

        HashMap<Category, Double> expenseSum = mExpenseSum.byCategory(expenses);

        assertEquals(0, expenseSum.size());
    }

    @Test
    public void testSumByExpenditureTypeWithNoMatchingExpenses() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(-1));
        expenses.add(getSimpleExpense(-2));
        expenses.add(getSimpleExpense(-3));
        expenses.add(getSimpleExpense(-4));
        expenses.add(getSimpleExpense(-5));
        expenses.add(getSimpleExpense(-6));

        assertEquals(0, mExpenseSum.byExpenditureType(false, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeTrue() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(-79));
        expenses.add(getSimpleExpense(-134));
        expenses.add(getSimpleExpense(42));
        expenses.add(getSimpleExpense(331));
        expenses.add(getSimpleExpense(754));
        expenses.add(getSimpleExpense(-100));

        assertEquals(-313, mExpenseSum.byExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeFalse() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(43));
        expenses.add(getSimpleExpense(-1337));
        expenses.add(getSimpleExpense(41));
        expenses.add(getSimpleExpense(54));
        expenses.add(getSimpleExpense(53));
        expenses.add(getSimpleExpense(312));

        assertEquals(-1337, mExpenseSum.byExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeWithChildren() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(445));

        ExpenseObject parent1 = getSimpleExpense(0);
        parent1.addChild(getSimpleExpense(-545));
        parent1.addChild(getSimpleExpense(48));
        parent1.addChild(getSimpleExpense(-605));
        expenses.add(parent1);

        ExpenseObject parent2 = getSimpleExpense(0);
        parent2.addChild(getSimpleExpense(878));
        parent2.addChild(getSimpleExpense(132));
        parent2.addChild(getSimpleExpense(4879));
        expenses.add(parent2);

        expenses.add(getSimpleExpense(48));
        expenses.add(getSimpleExpense(-500));

        assertEquals(-1650, mExpenseSum.byExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeIgnoresParentPrice() {
        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parentWithPrice = getSimpleExpense(120);
        parentWithPrice.addChild(getSimpleExpense(-312));
        parentWithPrice.addChild(getSimpleExpense(-111));

        expenses.add(parentWithPrice);

        assertEquals(0, mExpenseSum.byExpenditureType(false, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeWithEmptyList() {
        List<ExpenseObject> expenses = new ArrayList<>();

        double expenseSum = mExpenseSum.byExpenditureType(true, expenses);

        assertEquals(0, expenseSum, 0);
    }

    @Test
    public void testSumByMonth() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2018), 54));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JUNE, 2018), -54));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JUNE, 2017), 54));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JUNE, 2018), 103));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JULY, 2018), 10000));

        double sum = mExpenseSum.byMonth(expenses, Calendar.JUNE, 2018);

        assertEquals(49, sum, 0);
    }

    @Test
    public void testSumByMonthWithNoMatchingExpenses() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2018), 54));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.FEBRUARY, 2017), 54));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.MARCH, 2018), 54));

        double sum = mExpenseSum.byMonth(expenses, Calendar.FEBRUARY, 2018);

        assertEquals(0, sum, 0);
    }

    @Test
    public void testSumByMonthWithChildren() {
        List<ExpenseObject> expenses = new ArrayList<>();

        ExpenseObject parent = getSimpleExpense(0);
        parent.addChild(getExpenseWithDate(getSimpleDate(Calendar.OCTOBER, 2018), 858));
        parent.addChild(getExpenseWithDate(getSimpleDate(Calendar.OCTOBER, 2018), -487));
        parent.addChild(getExpenseWithDate(getSimpleDate(Calendar.OCTOBER, 2017), 45));
        expenses.add(parent);

        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.OCTOBER, 2018), -33));

        double sum = mExpenseSum.byMonth(expenses, Calendar.OCTOBER, 2018);

        assertEquals(338, sum, 0);
    }

    @Test
    public void testSum() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(1400));
        expenses.add(getSimpleExpense(-50));
        expenses.add(getSimpleExpense(-26));
        expenses.add(getSimpleExpense(13));

        double sum = mExpenseSum.sum(expenses);

        assertEquals(1337, sum, 0);
    }

    @Test
    public void testSumByYear() {
        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2000), 100));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2017), 780));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2018), -14));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2017), -23));
        expenses.add(getExpenseWithDate(getSimpleDate(Calendar.JANUARY, 2018), -16));

        HashMap<Integer, Double> yearSum = mExpenseSum.byYear(expenses);

        assertEquals(3, yearSum.size());
        assertEquals(100, yearSum.get(2000), 0);
        assertEquals(757, yearSum.get(2017), 0);
        assertEquals(-30, yearSum.get(2018), 0);
    }

    private ExpenseObject getExpenseWithCategory(Category category, double price) {
        return getExpense(price, category, getSimpleDate(Calendar.JANUARY, 2018));
    }

    private ExpenseObject getExpenseWithDate(Calendar date, double price) {
        return getExpense(price, getSimpleCategory("Kategorie"), date);
    }

    private ExpenseObject getSimpleExpense(double price) {
        return getExpense(price, getSimpleCategory("Kategorie"), getSimpleDate(Calendar.JANUARY, 2018));
    }

    private ExpenseObject getExpense(double price, Category category, Calendar date) {
        return new ExpenseObject(
                UUID.randomUUID(),
                "Buchung",
                new Price(price),
                date,
                category,
                "",
                UUID.randomUUID(),
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    private Category getSimpleCategory(String title) {
        return new Category(
                title,
                Color.black(),
                ExpenseType.deposit()
        );
    }

    private Calendar getSimpleDate(int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, month);
        date.set(Calendar.YEAR, year);

        return date;
    }
}
