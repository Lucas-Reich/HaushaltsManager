package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class RecyclerViewExpandableItemHandlerTest {
    private ExpenseListRecyclerViewAdapter mItemHandler;

    @Before
    public void setUp() {
        mItemHandler = new ExpenseListRecyclerViewAdapter(new ArrayList<IRecyclerItem>());

        RecyclerView rView = new RecyclerView(RuntimeEnvironment.application);
        rView.setAdapter(mItemHandler);
    }

    @After
    public void teardown() {
        mItemHandler = null;
    }

    @Test
    public void testInsertChildToExistingAndCollapsedParent() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentExpenseItem parentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2), parent);
        ChildExpenseItem expectedChildItem = new ChildExpenseItem(parentExpenseItem.getContent().getChildren().get(1), parentExpenseItem);
        parentExpenseItem.getChildren().remove(1);

        mItemHandler.insertItem(parentExpenseItem);
        assertEquals(2, mItemHandler.getItemCount());

        mItemHandler.insertItem(expectedChildItem);
        assertEquals(2, mItemHandler.getItemCount());
        assertEquals(2, mItemHandler.getItem(1).getChildren().size());
        assertTrue(mItemHandler.getItem(1).getChildren().contains(expectedChildItem));
    }

    @Test
    public void testInsertChildToExistingAndExpandedParent() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentExpenseItem parentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2), parent);
        ChildExpenseItem expectedChildItem = new ChildExpenseItem(parentExpenseItem.getContent().getChildren().get(1), parentExpenseItem);
        parentExpenseItem.getChildren().remove(1);

        mItemHandler.insertItem(parentExpenseItem);
        mItemHandler.toggleExpansion(1);
        assertEquals(3, mItemHandler.getItemCount());

        mItemHandler.insertItem(expectedChildItem);
        assertEquals(4, mItemHandler.getItemCount());
        assertEquals(2, mItemHandler.getItem(1).getChildren().size());
        assertTrue(mItemHandler.getItem(1).getChildren().contains(expectedChildItem));
    }

    @Test
    public void testInsertChildToNotExistingParentShouldInsertChildAsExpenseItem() {
        DateItem date = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));
        ParentExpenseObject parentExpense = getParentExpenseObject(1);
        ChildExpenseItem childItem = new ChildExpenseItem(parentExpense.getChildren().get(0), new ParentExpenseItem(parentExpense, date));

        mItemHandler.insertItem(childItem);

        assertEquals(2, mItemHandler.getItemCount());
        assertTrue(mItemHandler.getItem(1) instanceof ParentExpenseItem);
    }

    @Test
    public void testRemoveChildItemShouldSucceed() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(3), parent);

        mItemHandler.insertItem(expectedParentExpenseItem);
        mItemHandler.toggleExpansion(1);
        assertSame(5, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(2));

        assertSame(4, mItemHandler.getItemCount());
        assertSame(2, mItemHandler.getItem(1).getChildren().size());

        assertEquals(expectedParentExpenseItem, mItemHandler.getItem(1));
        assertEquals(expectedParentExpenseItem.getChildren().get(0).getContent(), mItemHandler.getItem(2).getContent());
        assertEquals(expectedParentExpenseItem.getChildren().get(1).getContent(), mItemHandler.getItem(3).getContent());
    }

    @Test
    public void testRemoveLastChildOfParentShouldRemoveParent() {
        DateItem date = new DateItem(createSimpleDate(10, Calendar.MAY, 2019));

        mItemHandler.insertItem(new ParentExpenseItem(getParentExpenseObject(1), date));
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(date.getContent()), date));
        mItemHandler.toggleExpansion(2);
        assertSame(4, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(3));

        assertSame(2, mItemHandler.getItemCount());
    }

    @Test
    public void testRemoveLastChildOfParentAsLastItemOfDateShouldRemoveAll() {
        ParentExpenseObject parentExpenseObject = getParentExpenseObject(1);

        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        mItemHandler.insertItem(new ParentExpenseItem(parentExpenseObject, parent));
        mItemHandler.toggleExpansion(1);
        assertSame(3, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(2));

        assertSame(0, mItemHandler.getItemCount());
    }

    @Test
    public void testToggleExpansionShouldOpenParentAndAddChildren() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2), parent);

        mItemHandler.insertItem(expectedParentExpenseItem);
        assertSame(2, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);
        assertSame(4, mItemHandler.getItemCount());

        assertTrue(mItemHandler.getItem(1).isExpanded());
        assertEquals(expectedParentExpenseItem, mItemHandler.getItem(1));
        assertEquals(expectedParentExpenseItem.getChildren().get(0), mItemHandler.getItem(2));
        assertEquals(expectedParentExpenseItem.getChildren().get(1), mItemHandler.getItem(3));
    }

    @Test
    public void testToggleExpansionShouldCloseParentAndRemoveChildren() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2), parent);

        mItemHandler.insertItem(expectedParentExpenseItem);
        mItemHandler.toggleExpansion(1);
        assertSame(4, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);

        assertFalse(mItemHandler.getItem(1).isExpanded());
        assertEquals(expectedParentExpenseItem, mItemHandler.getItem(1));
    }

    @Test
    public void testToggleExpansionOfNotExistingItemShouldFailWithIndexOutOfBoundsException() {
        assertSame(0, mItemHandler.getItemCount());

        try {

            mItemHandler.toggleExpansion(1337);
            fail("Could toggle expansion of not existing Item");
        } catch (IndexOutOfBoundsException e) {

            assertEquals("Index: 1337, Size: 0", e.getMessage());
        }
    }

    @Test
    public void testToggleExpansionOfItemWhichIsNotParentShouldBeIgnored() {
        DateItem date = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));
        ExpenseItem expenseItem = new ExpenseItem(createSimpleExpense(date.getContent()), date);

        mItemHandler.insertItem(expenseItem);
        assertSame(2, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);
        assertSame(2, mItemHandler.getItemCount());
    }

    private ParentExpenseObject getParentExpenseObject(int childCount) {
        ExpenseObject parentExpense = getParentExpenseWithChildren(childCount);

        return ParentExpenseObject.fromParentExpense(parentExpense);
    }

    private ExpenseObject getParentExpenseWithChildren(int childrenCount) {
        Calendar date = createSimpleDate(11, Calendar.JUNE, 2019);

        ExpenseObject parent = createSimpleExpense(date);

        for (int i = 0; i < childrenCount; i++) {
            parent.addChild(createSimpleExpense(date));
        }

        return parent;
    }

    private Calendar createSimpleDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);

        return date;
    }

    private ExpenseObject createSimpleExpense(Calendar date) {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                -1,
                "Ich bin eine Ausgabe",
                new Price(new Random().nextInt(), true, currency),
                date,
                new Category("Kategorie", "#000000", true, new ArrayList<Category>()),
                "",
                -1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                currency
        );
    }
}
