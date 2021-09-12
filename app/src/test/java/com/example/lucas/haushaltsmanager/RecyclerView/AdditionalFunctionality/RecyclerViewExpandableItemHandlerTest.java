package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

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

        ParentBookingItem parentBookingItem = new ParentBookingItem(getParentExpense(2), parent);
        ChildExpenseItem expectedChildItem = new ChildExpenseItem(parentBookingItem.getContent().getChildren().get(1), parentBookingItem);
        parentBookingItem.getChildren().remove(1);

        mItemHandler.insertItem(parentBookingItem);
        assertEquals(2, mItemHandler.getItemCount());

        mItemHandler.insertItem(expectedChildItem);
        assertEquals(2, mItemHandler.getItemCount());
        assertEquals(2, ((ParentBookingItem) mItemHandler.getItem(1)).getChildren().size());
        assertTrue(((ParentBookingItem) mItemHandler.getItem(1)).getChildren().contains(expectedChildItem));
    }

    @Test
    public void testInsertChildToExistingAndExpandedParent() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentBookingItem parentBookingItem = new ParentBookingItem(getParentExpense(2), parent);
        ChildExpenseItem expectedChildItem = new ChildExpenseItem(parentBookingItem.getContent().getChildren().get(1), parentBookingItem);
        parentBookingItem.getChildren().remove(1);

        mItemHandler.insertItem(parentBookingItem);
        mItemHandler.toggleExpansion(1);
        assertEquals(3, mItemHandler.getItemCount());

        mItemHandler.insertItem(expectedChildItem);
        assertEquals(4, mItemHandler.getItemCount());
        assertEquals(2, ((ParentBookingItem) mItemHandler.getItem(1)).getChildren().size());
        assertTrue(((ParentBookingItem) mItemHandler.getItem(1)).getChildren().contains(expectedChildItem));
    }

    @Test
    public void testInsertChildToNotExistingParentShouldInsertChildAsExpenseItem() {
        DateItem date = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));
        ParentBooking parentExpense = getParentExpense(1);
        ChildExpenseItem childItem = new ChildExpenseItem(parentExpense.getChildren().get(0), new ParentBookingItem(parentExpense, date));

        mItemHandler.insertItem(childItem);

        assertEquals(2, mItemHandler.getItemCount());
        assertTrue(mItemHandler.getItem(1) instanceof ParentBookingItem);
    }

    @Test
    public void testRemoveChildItemShouldSucceed() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentBookingItem expectedParentBookingItem = new ParentBookingItem(getParentExpense(3), parent);

        mItemHandler.insertItem(expectedParentBookingItem);
        mItemHandler.toggleExpansion(1);
        assertSame(5, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(2));

        assertSame(4, mItemHandler.getItemCount());
        assertSame(2, ((ParentBookingItem) mItemHandler.getItem(1)).getChildren().size());

        assertEquals(expectedParentBookingItem, mItemHandler.getItem(1));
        assertEquals(expectedParentBookingItem.getChildren().get(0).getContent(), mItemHandler.getItem(2).getContent());
        assertEquals(expectedParentBookingItem.getChildren().get(1).getContent(), mItemHandler.getItem(3).getContent());
    }

    @Test
    public void testRemoveLastChildOfParentShouldRemoveParent() {
        DateItem date = new DateItem(createSimpleDate(10, Calendar.MAY, 2019));

        mItemHandler.insertItem(new ParentBookingItem(getParentExpense(1), date));
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(date.getContent()), date));
        mItemHandler.toggleExpansion(2);
        assertSame(4, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(3));

        assertSame(2, mItemHandler.getItemCount());
    }

    @Test
    public void testRemoveLastChildOfParentAsLastItemOfDateShouldRemoveAll() {
        ParentBooking parentBooking = getParentExpense(1);

        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        mItemHandler.insertItem(new ParentBookingItem(parentBooking, parent));
        mItemHandler.toggleExpansion(1);
        assertSame(3, mItemHandler.getItemCount());

        mItemHandler.removeItem(mItemHandler.getItem(2));

        assertSame(0, mItemHandler.getItemCount());
    }

    @Test
    public void testToggleExpansionShouldOpenParentAndAddChildren() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentBookingItem expectedParentBookingItem = new ParentBookingItem(getParentExpense(2), parent);

        mItemHandler.insertItem(expectedParentBookingItem);
        assertSame(2, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);
        assertSame(4, mItemHandler.getItemCount());

        assertTrue(((ParentBookingItem) mItemHandler.getItem(1)).isExpanded());
        assertEquals(expectedParentBookingItem, mItemHandler.getItem(1));
        assertEquals(expectedParentBookingItem.getChildren().get(0), mItemHandler.getItem(2));
        assertEquals(expectedParentBookingItem.getChildren().get(1), mItemHandler.getItem(3));
    }

    @Test
    public void testToggleExpansionShouldCloseParentAndRemoveChildren() {
        DateItem parent = new DateItem(createSimpleDate(10, Calendar.JUNE, 2019));

        ParentBookingItem expectedParentBookingItem = new ParentBookingItem(getParentExpense(2), parent);

        mItemHandler.insertItem(expectedParentBookingItem);
        mItemHandler.toggleExpansion(1);
        assertSame(4, mItemHandler.getItemCount());

        mItemHandler.toggleExpansion(1);

        assertFalse(((ParentBookingItem) mItemHandler.getItem(1)).isExpanded());
        assertEquals(expectedParentBookingItem, mItemHandler.getItem(1));
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

    private ParentBooking getParentExpense(int childrenCount) {
        Calendar date = createSimpleDate(11, Calendar.JUNE, 2019);

        ParentBooking parent = new ParentBooking(
                UUID.randomUUID(),
                "",
                date,
                new ArrayList<>()
        );

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

    private Booking createSimpleExpense(Calendar date) {
        return new Booking(
                UUID.randomUUID(),
                "Ich bin eine Ausgabe",
                new Price(new Random().nextInt(), true),
                date,
                new Category("Kategorie", Color.Companion.black(), ExpenseType.Companion.expense()),
                "",
                UUID.randomUUID(),
                Booking.EXPENSE_TYPES.NORMAL_EXPENSE
        );
    }
}
