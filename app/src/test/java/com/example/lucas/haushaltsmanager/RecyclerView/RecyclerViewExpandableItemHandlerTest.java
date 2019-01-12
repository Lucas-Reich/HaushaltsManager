package com.example.lucas.haushaltsmanager.RecyclerView;

import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
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
        ParentExpenseItem parentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2));
        ChildItem expectedChildItem = new ChildItem(parentExpenseItem.getContent().getChildren().get(1), parentExpenseItem.getContent().getParent().getIndex());
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
        ParentExpenseItem parentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2));
        ChildItem expectedChildItem = new ChildItem(parentExpenseItem.getContent().getChildren().get(1), parentExpenseItem.getContent().getParent().getIndex());
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
        ParentExpenseObject parentExpense = getParentExpenseObject(1);
        ChildItem childItem = new ChildItem(parentExpense.getChildren().get(0), parentExpense.getParent().getIndex());

        mItemHandler.insertItem(childItem);

        assertEquals(2, mItemHandler.getItemCount());
        assertTrue(mItemHandler.getItem(1) instanceof ExpenseItem);
    }

    @Test
    public void testRemoveChildItemShouldSucceed() {
        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(3));

        mItemHandler.insertItem(expectedParentExpenseItem);
        mItemHandler.toggleExpansion(1);
        assertSame(5, mItemHandler.getItemCount());

        mItemHandler.removeItem(2);

        assertSame(4, mItemHandler.getItemCount());
        assertSame(2, mItemHandler.getItem(1).getChildren().size());

        assertEquals(expectedParentExpenseItem, mItemHandler.getItem(1));
        assertEquals(expectedParentExpenseItem.getChildren().get(0).getContent(), mItemHandler.getItem(2).getContent());
        assertEquals(expectedParentExpenseItem.getChildren().get(1).getContent(), mItemHandler.getItem(3).getContent());
    }

    @Test
    public void testRemoveLastChildOfParentShouldRemoveParent() {
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense()));
        mItemHandler.insertItem(new ParentExpenseItem(getParentExpenseObject(1)));
        mItemHandler.toggleExpansion(2);
        assertSame(4, mItemHandler.getItemCount());

        mItemHandler.removeItem(3);

        assertSame(2, mItemHandler.getItemCount());
    }

    @Test
    public void testRemoveLastChildOfParentAsLastItemOfDateShouldRemoveAll() {
        mItemHandler.insertItem(new ParentExpenseItem(getParentExpenseObject(1)));
        mItemHandler.toggleExpansion(1);
        assertSame(3, mItemHandler.getItemCount());

        mItemHandler.removeItem(2);

        assertSame(0, mItemHandler.getItemCount());
    }

    @Test
    public void testToggleExpansionShouldOpenParentAndAddChildren() {
        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2));

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
        ParentExpenseItem expectedParentExpenseItem = new ParentExpenseItem(getParentExpenseObject(2));

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

            assertEquals("Could not find Item at position 1337", e.getMessage());
        }
    }

    @Test
    public void testToggleExpansionOfItemWhichIsNotParentShouldBeIgnored() {
        ExpenseItem expenseItem = new ExpenseItem(createSimpleExpense());

        mItemHandler.insertItem(expenseItem);
        assertSame(2, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);
        assertSame(2, mItemHandler.getItemCount());
    }

    private ParentExpenseObject getParentExpenseObject(int childCount) {
        ExpenseObject parentExpense = getParentExpenseWithChildren(childCount);

        return new ParentExpenseObject(parentExpense, parentExpense.getChildren());
    }

    private ExpenseObject getParentExpenseWithChildren(int childrenCount) {
        ExpenseObject parent = createSimpleExpense();

        for (int i = 0; i < childrenCount; i++) {
            parent.addChild(createSimpleExpense());
        }

        return parent;
    }

    private ExpenseObject createSimpleExpense() {
        return new ExpenseObject(
                -1,
                "Ich bin eine Ausgabe",
                new Random().nextInt(),
                Calendar.getInstance(),
                true,
                new Category("Kategorie", "#000000", true, new ArrayList<Category>()),
                "",
                -1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                new Currency("Euro", "EUR", "€")
        );
    }
}
