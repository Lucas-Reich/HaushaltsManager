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
