package com.example.lucas.haushaltsmanager.RecyclerView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class RecyclerViewItemHolderTest {
    private ExpenseListRecyclerViewAdapter mItemHandler;

    // TODO: Was soll dieser Test überhaupt testen?
    //  Soll er die RecyclerViewItemHandler Klasse testen?
    //      Wenn ja muss ich aufpassen nicht die InsertStrategy zu testen, sondern mehr überliegende funktionalitäten,
    //      wie z.B.: das dynamische erstellen von Parents, wenn diese nicht in der Liste sind.
    //  Falls nicht ist dieser Test recht sinnlos

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
    public void testGetItemShouldFindItem() {
        DateItem dateItem = new DateItem(createSimpleDate(2019, Calendar.MAY, 11));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);
        mItemHandler.insertItem(expectedItem);

        IRecyclerItem actualItem = mItemHandler.getItem(1);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testGetItemShouldThrowExceptionForNotExistingItem() {
        try {
            mItemHandler.getItem(1337);
            fail("Should not find Item with index 1337");
        } catch (IndexOutOfBoundsException e) {

            assertEquals("Index: 1337, Size: 0", e.getMessage());
        }
    }

    @Test
    public void testCorrectDateOrder() {
        DateItem fifthDateItem = new DateItem(createSimpleDate(2018, Calendar.JUNE, 11));
        IRecyclerItem item1 = new ExpenseItem(createSimpleExpense(fifthDateItem.getContent()), fifthDateItem);
        mItemHandler.insertItem(item1);

        DateItem secondDateItem = new DateItem(createSimpleDate(2019, Calendar.DECEMBER, 30));
        IRecyclerItem item2 = new ExpenseItem(createSimpleExpense(secondDateItem.getContent()), secondDateItem);
        mItemHandler.insertItem(item2);

        DateItem firstDateItem = new DateItem(createSimpleDate(2019, Calendar.DECEMBER, 31));
        IRecyclerItem item3 = new ExpenseItem(createSimpleExpense(firstDateItem.getContent()), firstDateItem);
        mItemHandler.insertItem(item3);

        DateItem thirdDateItem = new DateItem(createSimpleDate(2019, Calendar.JUNE, 11));
        IRecyclerItem item4 = new ExpenseItem(createSimpleExpense(thirdDateItem.getContent()), thirdDateItem);
        mItemHandler.insertItem(item4);

        DateItem fourthDateItem = new DateItem(createSimpleDate(2019, Calendar.MAY, 6));
        IRecyclerItem item5 = new ExpenseItem(createSimpleExpense(fourthDateItem.getContent()), fourthDateItem);
        mItemHandler.insertItem(item5);

        assertEquals(10, mItemHandler.getItemCount());
        assertSameDate(firstDateItem, (DateItem) mItemHandler.getItem(0));
        assertSameDate(secondDateItem, (DateItem) mItemHandler.getItem(2));
        assertSameDate(thirdDateItem, (DateItem) mItemHandler.getItem(4));
        assertSameDate(fourthDateItem, (DateItem) mItemHandler.getItem(6));
        assertSameDate(fifthDateItem, (DateItem) mItemHandler.getItem(8));
    }

    @Test
    public void testInsertWithSingleExpense() {
        DateItem expectedDateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 1));
        ExpenseItem expectedRecyclerItem = new ExpenseItem(createSimpleExpense(expectedDateItem.getContent()), expectedDateItem);

        mItemHandler.insertItem(expectedRecyclerItem);

        assertSame(2, mItemHandler.getItemCount());
        assertSameDate(expectedDateItem, (DateItem) mItemHandler.getItem(0));
        assertEquals(expectedRecyclerItem, mItemHandler.getItem(1));
    }

    @Test
    public void testInsertItemAfterSet() {
        List<DateItem> dateItems = new ArrayList<>();
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 22)));
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 24)));
        fillHandlerWithItems(dateItems);

        DateItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 21));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, (DateItem) mItemHandler.getItem(4));
        assertEquals(expectedItem, mItemHandler.getItem(5));
    }

    @Test
    public void testInsertItemInTheMiddleOfSet() {
        List<DateItem> dateItems = new ArrayList<>();
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 22)));
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 24)));
        fillHandlerWithItems(dateItems);

        DateItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 23));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, (DateItem) mItemHandler.getItem(2));
        assertEquals(expectedItem, mItemHandler.getItem(3));
    }

    @Test
    public void testInsertItemBeforeSet() {
        List<DateItem> dateItems = new ArrayList<>();
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 22)));
        dateItems.add(new DateItem(createSimpleDate(2019, Calendar.JANUARY, 24)));
        fillHandlerWithItems(dateItems);

        DateItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 25));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, (DateItem) mItemHandler.getItem(0));
        assertEquals(expectedItem, mItemHandler.getItem(1));
    }

    // TODO: testRemoveExpandedParentShouldRemoveChildren

    @Test
    public void testInsertToExistingDate() {
        DateItem parent = new DateItem(createSimpleDate(2019, Calendar.MAY, 6));
        ExpenseItem existingItem = new ExpenseItem(createSimpleExpense(parent.getContent()), parent);
        mItemHandler.insertItem(existingItem);

        ExpenseItem expectedItem = new ExpenseItem(createSimpleExpense(parent.getContent()), parent);
        mItemHandler.insertItem(expectedItem);

        assertSame(3, mItemHandler.getItemCount());
        assertSameDate(parent, (DateItem) mItemHandler.getItem(0));
        assertEquals(existingItem, mItemHandler.getItem(1));
        assertEquals(expectedItem, mItemHandler.getItem(2));
    }

    @Test
    public void testInsertToExistingDate2() {
        DateItem parent = new DateItem(createSimpleDate(2019, Calendar.MAY, 6));

        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(createSimpleDate(2019, Calendar.MAY, 6)), parent));

        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(createSimpleDate(2019, Calendar.MAY, 6)), parent);
        mItemHandler.insertItem(expectedItem);

        assertSame(3, mItemHandler.getItemCount());
        assertEquals(expectedItem, mItemHandler.getItem(2));
    }

    @Test
    public void testRemoveExistingItemShouldSucceed() {
        DateItem dateItem = new DateItem(createSimpleDate(2019, 5, 11));
        ExpenseItem expense1 = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);
        ExpenseItem expense2 = new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem);

        mItemHandler.insertItem(expense1);
        mItemHandler.insertItem(expense2);

        assertEquals(3, mItemHandler.getItemCount());

        mItemHandler.removeItem(expense1);

        assertEquals(2, mItemHandler.getItemCount());
    }

    @Test
    public void testRemoveLastItemShouldDeleteDateItem() {
        DateItem parent = new DateItem(createSimpleDate(2019, 0, 24));
        ExpenseItem item = new ExpenseItem(createSimpleExpense(parent.getContent()), parent);

        mItemHandler.insertItem(item);

        assertEquals(2, mItemHandler.getItemCount());

        mItemHandler.removeItem(item);

        assertEquals(0, mItemHandler.getItemCount());
    }

    private Calendar createSimpleDate(int year, int month, int day) {
        Calendar secureDate = Calendar.getInstance();
        secureDate.set(Calendar.YEAR, year);
        secureDate.set(Calendar.MONTH, month);
        secureDate.set(Calendar.DAY_OF_MONTH, day);
        secureDate.set(Calendar.HOUR_OF_DAY, 0);
        secureDate.set(Calendar.MINUTE, 0);
        secureDate.set(Calendar.SECOND, 0);
        secureDate.set(Calendar.MILLISECOND, 0);

        return secureDate;
    }

    private ExpenseObject createSimpleExpense(Calendar expenseDate) {
        return new ExpenseObject(
                UUID.randomUUID(),
                "Ich bin eine Ausgabe",
                new Price(105, true),
                expenseDate,
                new Category("Kategorie", Color.black(), ExpenseType.expense()),
                "",
                UUID.randomUUID(),
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    private void assertSameDate(DateItem cal1, DateItem cal2) {
        boolean isSameDate = cal1.getContent().get(Calendar.YEAR) == cal2.getContent().get(Calendar.YEAR) &&
                cal1.getContent().get(Calendar.DAY_OF_YEAR) == cal2.getContent().get(Calendar.DAY_OF_YEAR);

        if (!isSameDate) {
            fail(String.format(
                    "Date %s does not equals Date %s",
                    CalendarUtils.formatHumanReadable(cal1.getContent()),
                    CalendarUtils.formatHumanReadable(cal2.getContent())
            ));
        }
    }

    private void fillHandlerWithItems(List<DateItem> itemDates) {
        for (DateItem dateItem : itemDates) {
            mItemHandler.insertItem(
                    new ExpenseItem(createSimpleExpense(dateItem.getContent()), dateItem)
            );
        }
    }
}
