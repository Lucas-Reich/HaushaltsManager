package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.MockInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.MockSelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildExpenseItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ExpenseItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentExpenseItem.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.MockItemHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class RecyclerViewSelectedItemHandlerTest {
    private MockItemHandler itemHandler;

    @Before
    public void setUp() {
        itemHandler = new MockItemHandler(new MockInsertStrategy(), new MockSelectionRules());
    }

    @After
    public void teardown() {
        itemHandler = null;
    }

    public void notSelectedItemCanBeSelected() {
    }

    public void selectedItemCannotBeSelected() {
    }

    public void selectionsCanBeCleared() {
    }

    public void selectedItemCountReturnsCorrectCount() {
    }

    @Test
    public void notSelectedItemCanBeUnselected() {
        ChildExpenseItem notSelectedItem = new ChildExpenseItem(getDummyExpense(), getDummyParentItem());

        itemHandler.unselectItem(notSelectedItem, 0);

        assertFalse(itemHandler.isItemSelected(notSelectedItem));
    }

    @Test
    public void deletedItemShouldBeRemovedFromSelectedList() {
        ExpenseItem item = new ExpenseItem(getDummyExpense(), getDummyDate());

        itemHandler.insertItem(item);

        itemHandler.selectItem(item, 1);
        assertTrue(itemHandler.isItemSelected(item));

        itemHandler.removeItem(item);
        assertEquals(itemHandler.getItemCount(), 0);

        assertEquals(0, itemHandler.getSelectedItemCount());
        assertFalse(itemHandler.isItemSelected(item));
    }

    @Test
    public void childItemStaysSelectedWhenParentIsClosedAndOpened() {
        ParentExpenseItem parent = getDummyParentItem();

        ChildExpenseItem child = new ChildExpenseItem(getDummyExpense(), parent);
        parent.addChild(child);

        itemHandler.insertItem(parent);

        itemHandler.toggleExpansion(1);
        assertEquals(3, itemHandler.getItemCount());

        itemHandler.selectItem(child, 2);
        assertTrue(itemHandler.isItemSelected(child));

        itemHandler.toggleExpansion(1);
        assertEquals(2, itemHandler.getItemCount());

        assertEquals(1, itemHandler.getSelectedItemCount());
        assertTrue(itemHandler.isItemSelected(child));
    }

    private ParentExpenseItem getDummyParentItem() {
        return new ParentExpenseItem(
                new ParentExpenseObject
                        (
                                ExpensesDbHelper.INVALID_INDEX,
                                "Ausgabe",
                                Calendar.getInstance(),
                                new ArrayList<ExpenseObject>()
                        ),
                getDummyDate()
        );
    }

    private DateItem getDummyDate() {
        return new DateItem(
                Calendar.getInstance()
        );
    }

    private ExpenseObject getDummyExpense() {
        return new ExpenseObject(
                "Ausgabe",
                new Price(100),
                new Category("Kategorie", new Color(Color.WHITE), ExpenseType.expense()),
                UUID.randomUUID()
        );
    }
}
