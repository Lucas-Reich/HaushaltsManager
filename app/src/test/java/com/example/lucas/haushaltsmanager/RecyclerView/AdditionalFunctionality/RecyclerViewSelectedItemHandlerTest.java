package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.MockInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.MockSelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.MockItemHandler;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.Price;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

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

        itemHandler.unselect(notSelectedItem);

        assertFalse(itemHandler.isSelected(notSelectedItem));
    }

    @Test
    public void deletedItemShouldBeRemovedFromSelectedList() {
        ExpenseItem item = new ExpenseItem(getDummyExpense(), getDummyDate());

        itemHandler.insert(item);

        itemHandler.select(item);
        assertTrue(itemHandler.isSelected(item));

        itemHandler.remove(item);
        assertEquals(itemHandler.getItemCount(), 0);

        assertEquals(0, itemHandler.getSelectedItemCount());
        assertFalse(itemHandler.isSelected(item));
    }

    @Test
    public void childItemStaysSelectedWhenParentIsClosedAndOpened() {
        ParentBookingItem parent = getDummyParentItem();

        ChildExpenseItem child = new ChildExpenseItem(getDummyExpense(), parent);
        parent.addChild(child);

        itemHandler.insert(parent);

        itemHandler.toggleExpansion(parent);
        assertEquals(3, itemHandler.getItemCount());

        itemHandler.select(child);
        assertTrue(itemHandler.isSelected(child));

        itemHandler.toggleExpansion(parent);
        assertEquals(2, itemHandler.getItemCount());

        assertEquals(1, itemHandler.getSelectedItemCount());
        assertTrue(itemHandler.isSelected(child));
    }

    private ParentBookingItem getDummyParentItem() {
        return new ParentBookingItem(
                new ParentBooking(
                        UUID.randomUUID(),
                        Calendar.getInstance(),
                        "Ausgabe",
                        new ArrayList<>()
                ), getDummyDate());
    }

    private DateItem getDummyDate() {
        return new DateItem(
                Calendar.getInstance()
        );
    }

    private Booking getDummyExpense() {
        return new Booking(
                "Ausgabe",
                new Price(100),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
