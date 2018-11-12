package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

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
        Category category1 = getSimpleCategory(1, "Kategorie 1");
        Category category2 = getSimpleCategory(2, "Kategorie 2");
        Category category3 = getSimpleCategory(3, "Kategorie 3");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category3, 113, false));
        expenses.add(getSimpleExpense(category2, 77, false));
        expenses.add(getSimpleExpense(category2, 23, false));
        expenses.add(getSimpleExpense(category3, 100, false));
        expenses.add(getSimpleExpense(category3, 9, false));
        expenses.add(getSimpleExpense(category3, 91, false));
        expenses.add(getSimpleExpense(category1, 1337, false));

        HashMap<Category, Double> categorySum = mExpenseSum.sumBookingsByCategory(expenses);

        assertEquals(1337, categorySum.get(category1), 0);
        assertEquals(100, categorySum.get(category2), 0);
        assertEquals(313, categorySum.get(category3), 0);
    }

    @Test
    public void testSumByCategoriesWithPositiveValues() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 170, false));
        expenses.add(getSimpleExpense(category, 1003, false));
        expenses.add(getSimpleExpense(category, 163, false));
        expenses.add(getSimpleExpense(category, 1, false));

        HashMap<Category, Double> categorySum = mExpenseSum.sumBookingsByCategory(expenses);

        assertEquals(1337, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoryWithNegativeValues() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 100, true));
        expenses.add(getSimpleExpense(category, 17, true));
        expenses.add(getSimpleExpense(category, 87, true));

        HashMap<Category, Double> categorySum = mExpenseSum.sumBookingsByCategory(expenses);

        assertEquals(-204, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoriesWithChildren() {
        Category category = getSimpleCategory(1, "Kategorie");

        ExpenseObject expenseWithChildren = getSimpleExpense(category, 0, false);
        expenseWithChildren.addChild(getSimpleExpense(category, 1000, false));
        expenseWithChildren.addChild(getSimpleExpense(category, 1500, false));
        expenseWithChildren.addChild(getSimpleExpense(category, 174, false));

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(expenseWithChildren);
        expenses.add(getSimpleExpense(category, 1337, true));

        HashMap<Category, Double> categorySum = mExpenseSum.sumBookingsByCategory(expenses);

        assertEquals(1337, categorySum.get(category), 0);
    }

    @Test
    public void testSumByCategoriesWithEmptyList() {
        List<ExpenseObject> expenses = new ArrayList<>();

        HashMap<Category, Double> expenseSum = mExpenseSum.sumBookingsByCategory(expenses);

        assertEquals(0, expenseSum.size());
    }

    @Test
    public void testSumByExpenditureTypeWithNoMatchingExpenses() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 1, true));
        expenses.add(getSimpleExpense(category, 2, true));
        expenses.add(getSimpleExpense(category, 3, true));
        expenses.add(getSimpleExpense(category, 4, true));
        expenses.add(getSimpleExpense(category, 5, true));
        expenses.add(getSimpleExpense(category, 6, true));

        assertEquals(0, mExpenseSum.sumBookingsByExpenditureType(false, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeTrue() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 79, true));
        expenses.add(getSimpleExpense(category, 134, true));
        expenses.add(getSimpleExpense(category, 42, false));
        expenses.add(getSimpleExpense(category, 331, false));
        expenses.add(getSimpleExpense(category, 754, false));
        expenses.add(getSimpleExpense(category, 100, true));

        assertEquals(-313, mExpenseSum.sumBookingsByExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeFalse() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 43, false));
        expenses.add(getSimpleExpense(category, 1337, true));
        expenses.add(getSimpleExpense(category, 41, false));
        expenses.add(getSimpleExpense(category, 54, false));
        expenses.add(getSimpleExpense(category, 53, false));
        expenses.add(getSimpleExpense(category, 312, false));

        assertEquals(-1337, mExpenseSum.sumBookingsByExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeWithChildren() {
        Category category = getSimpleCategory(1, "Kategorie");

        List<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(getSimpleExpense(category, 445, false));

        ExpenseObject parent1 = getSimpleExpense(category, 0, true);
        parent1.addChild(getSimpleExpense(category, 545, true));
        parent1.addChild(getSimpleExpense(category, 48, false));
        parent1.addChild(getSimpleExpense(category, 605, true));
        expenses.add(parent1);

        ExpenseObject parent2 = getSimpleExpense(category, 0, true);
        parent2.addChild(getSimpleExpense(category, 878, false));
        parent2.addChild(getSimpleExpense(category, 132, false));
        parent2.addChild(getSimpleExpense(category, 4879, false));
        expenses.add(parent2);

        expenses.add(getSimpleExpense(category, 48, false));
        expenses.add(getSimpleExpense(category, 500, true));

        assertEquals(-1650, mExpenseSum.sumBookingsByExpenditureType(true, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeIgnoresParentPrice() {
        Category category = getSimpleCategory(1, "Category");

        List<ExpenseObject> expenses = new ArrayList<>();
        ExpenseObject parentWithPrice = getSimpleExpense(category, 120, false);
        parentWithPrice.addChild(getSimpleExpense(category, 312, true));
        parentWithPrice.addChild(getSimpleExpense(category, 111, true));

        expenses.add(parentWithPrice);

        assertEquals(0, mExpenseSum.sumBookingsByExpenditureType(false, expenses), 0);
    }

    @Test
    public void testSumByExpenditureTypeWithEmptyList() {
        List<ExpenseObject> expenses = new ArrayList<>();

        double expenseSum = mExpenseSum.sumBookingsByExpenditureType(true, expenses);

        assertEquals(0, expenseSum, 0);
    }

    private ExpenseObject getSimpleExpense(Category category, double price, boolean expenditure) {
        return new ExpenseObject(
                "Ausgabe",
                price,
                expenditure,
                category,
                -1,
                mock(Currency.class)
        );
    }

    private Category getSimpleCategory(long index, String title) {
        return new Category(
                index,
                title,
                "#000000",
                false,
                new ArrayList<Category>()
        );
    }
}
