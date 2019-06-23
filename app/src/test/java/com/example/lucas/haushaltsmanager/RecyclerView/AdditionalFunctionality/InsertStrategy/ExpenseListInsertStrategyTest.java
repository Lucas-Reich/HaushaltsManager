package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpenseListInsertStrategyTest {
    private ExpenseListInsertStrategy insertStrategy = new ExpenseListInsertStrategy();

    @Test
    public void newDateItemIsInsertedAtCorrectPosition() {
        List<IRecyclerItem> items = new ArrayList<>();
        items.add(new DateItem(createDate(17, Calendar.JUNE, 2019)));
        items.add(new ExpenseItem(createDummyExpense(), null));
        items.add(new DateItem(createDate(15, Calendar.JUNE, 2019)));
        items.add(new DateItem(createDate(15, Calendar.JANUARY, 2019)));


        DateItem dateItem = new DateItem(createDate(14, Calendar.JUNE, 2019));
        int insertIndex = insertStrategy.insert(dateItem, items);


        assertEquals(5, items.size());
        assertEquals(insertIndex, items.indexOf(dateItem));
        assertEquals(dateItem, items.get(3));
    }

    @Test
    public void cannotAddWrongClassAsParent() {
        try {
            insertStrategy.insert(new ExpenseItem(null, null), new ArrayList<IRecyclerItem>());

            Assert.fail("ExpenseItem could be registered as parent");
        } catch (IllegalArgumentException e) {

            assertEquals("ExpenseListInsertStrategy requires DateItem as parents. Class given: ExpenseItem", e.getMessage());
        }
    }

    @Test
    public void itemWithExistingParentShouldBeInsertedAfterwards() {
        DateItem dateItem = new DateItem(createDate(1, Calendar.JANUARY, 2019));
        List<IRecyclerItem> items = new ArrayList<>();
        items.add(dateItem);


        ExpenseItem expenseItem = new ExpenseItem(createDummyExpense(), dateItem);
        int insertIndex = insertStrategy.insert(expenseItem, items);


        assertEquals(2, items.size());
        assertEquals(insertIndex, items.indexOf(expenseItem));
        assertEquals(expenseItem, items.get(1));
    }

    @Test
    public void itemWithExistingAndNotExpandedParentShouldBeAddedToParent() {
        List<IRecyclerItem> items = new ArrayList<>();

        DateItem dateItem = new DateItem(createDate(1, Calendar.JANUARY, 2019));
        items.add(dateItem);

        ParentExpenseItem parentItem = new ParentExpenseItem(createDummyParentExpense(), dateItem);
        parentItem.setExpanded(false); // ParentItem is not expanded
        items.add(parentItem);


        ChildExpenseItem childItem = new ChildExpenseItem(createDummyExpense(), parentItem);
        int insertIndex = insertStrategy.insert(childItem, items);


        assertEquals(2, items.size());
        assertEquals(insertIndex, InsertStrategy.INVALID_INDEX);
        assertTrue(items.get(1).getChildren().contains(childItem));
    }

    @Test
    public void itemWithExistingAndExpandedParentShouldBeAddedAfterwardsAndToParent() {
        List<IRecyclerItem> items = new ArrayList<>();

        DateItem dateItem = new DateItem(createDate(1, Calendar.JANUARY, 2019));
        items.add(dateItem);

        ParentExpenseItem parentItem = new ParentExpenseItem(createDummyParentExpense(), dateItem);
        parentItem.setExpanded(true); // ParentItem is expanded
        items.add(parentItem);


        ChildExpenseItem childItem = new ChildExpenseItem(createDummyExpense(), parentItem);
        int insertIndex = insertStrategy.insert(childItem, items);


        assertEquals(3, items.size());
        assertEquals(insertIndex, items.indexOf(childItem));
        assertEquals(childItem, items.get(2));

        assertTrue(items.get(1).getChildren().contains(childItem));
    }

    @Test
    public void itemWithNotExistingParentCannotBeAddedToListAndReturnsInvalidIndex() {
        ExpenseItem expenseItem = new ExpenseItem(createDummyExpense(), new DateItem(createDate(11, Calendar.JUNE, 2019)));

        List<IRecyclerItem> items = new ArrayList<>();
        int insertIndex = insertStrategy.insert(expenseItem, items);

        assertEquals(0, items.size());
        assertEquals(InsertStrategy.INVALID_INDEX, insertIndex);
    }

    @Test
    public void itemWithNotExistingParentCannotBeAddedToListAndReturnsInvalidIndex2() {
        ParentExpenseItem notInListParent = new ParentExpenseItem(createDummyParentExpense(), null);
        notInListParent.setExpanded(true);

        ChildExpenseItem child = new ChildExpenseItem(createDummyExpense(), notInListParent);

        List<IRecyclerItem> items = new ArrayList<>();
        int insertIndex = insertStrategy.insert(child, items);

        assertEquals(0, items.size());
        assertEquals(InsertStrategy.INVALID_INDEX, insertIndex);
    }

    private ParentExpenseObject createDummyParentExpense() {
        return new ParentExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                "ParentAusgabe",
                new Currency("Euro", "EUR", "€"),
                createDate(1, Calendar.JANUARY, 2019),
                new ArrayList<ExpenseObject>()
        );
    }

    private ExpenseObject createDummyExpense() {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(100, false, currency),
                new Category("Kategorie", new Color(Color.WHITE), true, new ArrayList<Category>()),
                ExpensesDbHelper.INVALID_INDEX,
                currency
        );
    }

    private Calendar createDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 0, 0, 0);

        return date;
    }
}
