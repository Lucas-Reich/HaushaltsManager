package com.example.lucas.haushaltsmanager.RecyclerView;

import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class RecyclerViewItemHolderTest {
    private ExpenseListRecyclerViewAdapter mItemHandler;

    private static Calendar createSimpleDate(int year, int month, int day) {
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

    private static ExpenseObject createSimpleExpense(Calendar expenseDate) {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                -1,
                "Ich bin eine Ausgabe",
                new Price(105, true, currency),
                expenseDate,
                new Category("Kategorie", "#000000", true, new ArrayList<Category>()),
                "",
                -1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                currency);
    }

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
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(createSimpleDate(2019, 5, 11)));
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

            assertEquals("Could not find Item at position 1337", e.getMessage());
        }
    }

    @Test
    public void testCorrectDateOrder() {
        IRecyclerItem fifthDateItem = new DateItem(createSimpleDate(2018, Calendar.JUNE, 11));
        IRecyclerItem item1 = new ExpenseItem(createSimpleExpense(fifthDateItem.getDate()));
        mItemHandler.insertItem(item1);

        IRecyclerItem secondDateItem = new DateItem(createSimpleDate(2019, Calendar.DECEMBER, 30));
        IRecyclerItem item2 = new ExpenseItem(createSimpleExpense(secondDateItem.getDate()));
        mItemHandler.insertItem(item2);

        IRecyclerItem firstDateItem = new DateItem(createSimpleDate(2019, Calendar.DECEMBER, 31));
        IRecyclerItem item3 = new ExpenseItem(createSimpleExpense(firstDateItem.getDate()));
        mItemHandler.insertItem(item3);

        IRecyclerItem thirdDateItem = new DateItem(createSimpleDate(2019, Calendar.JUNE, 11));
        IRecyclerItem item4 = new ExpenseItem(createSimpleExpense(thirdDateItem.getDate()));
        mItemHandler.insertItem(item4);

        IRecyclerItem fourthDateItem = new DateItem(createSimpleDate(2019, Calendar.MAY, 6));
        IRecyclerItem item5 = new ExpenseItem(createSimpleExpense(fourthDateItem.getDate()));
        mItemHandler.insertItem(item5);

        assertEquals(10, mItemHandler.getItemCount());
        assertSameDate(firstDateItem, mItemHandler.getItem(0));
        assertSameDate(secondDateItem, mItemHandler.getItem(2));
        assertSameDate(thirdDateItem, mItemHandler.getItem(4));
        assertSameDate(fourthDateItem, mItemHandler.getItem(6));
        assertSameDate(fifthDateItem, mItemHandler.getItem(8));
    }

    @Test
    public void testInsertWithSingleExpense() {
        DateItem expectedDateItem = new DateItem(
                createSimpleDate(2019, Calendar.JANUARY, 1)
        );
        ExpenseItem expectedRecyclerItem = new ExpenseItem(createSimpleExpense(
                expectedDateItem.getDate()
        ));

        mItemHandler.insertItem(expectedRecyclerItem);

        assertSame(2, mItemHandler.getItemCount());
        assertSameDate(expectedDateItem, mItemHandler.getItem(0));
        assertEquals(expectedRecyclerItem, mItemHandler.getItem(1));
    }

    @Test
    public void testInsertItemAfterSet() {
        List<Calendar> dateItems = new ArrayList<>();
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 22));
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 24));
        fillHandlerWithItems(dateItems);

        IRecyclerItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 21));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getDate()));

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, mItemHandler.getItem(4));
        assertEquals(expectedItem, mItemHandler.getItem(5));
    }

    @Test
    public void testInsertItemInTheMiddleOfSet() {
        List<Calendar> dateItems = new ArrayList<>();
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 22));
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 24));
        fillHandlerWithItems(dateItems);

        IRecyclerItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 23));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getDate()));

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, mItemHandler.getItem(2));
        assertEquals(expectedItem, mItemHandler.getItem(3));
    }

    @Test
    public void testInsertItemBeforeSet() {
        List<Calendar> dateItems = new ArrayList<>();
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 22));
        dateItems.add(createSimpleDate(2019, Calendar.JANUARY, 24));
        fillHandlerWithItems(dateItems);

        IRecyclerItem dateItem = new DateItem(createSimpleDate(2019, Calendar.JANUARY, 25));
        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(dateItem.getDate()));

        mItemHandler.insertItem(expectedItem);

        assertSame(6, mItemHandler.getItemCount());
        assertSameDate(dateItem, mItemHandler.getItem(0));
        assertEquals(expectedItem, mItemHandler.getItem(1));
    }

    @Test
    public void testInsertToExistingDate() {
        IRecyclerItem existingItemDate = new DateItem(createSimpleDate(2019, Calendar.MAY, 6));
        IRecyclerItem existingItem = new ExpenseItem(createSimpleExpense(existingItemDate.getDate()));
        mItemHandler.insertItem(existingItem);

        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(existingItemDate.getDate()));
        mItemHandler.insertItem(expectedItem);

        assertSame(3, mItemHandler.getItemCount());
        assertSameDate(existingItemDate, mItemHandler.getItem(0));
        assertEquals(existingItem, mItemHandler.getItem(1));
        assertEquals(expectedItem, mItemHandler.getItem(2));
    }

    @Test
    public void testInsertToExistingDate2() {
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(createSimpleDate(2019, Calendar.MAY, 6))));

        IRecyclerItem expectedItem = new ExpenseItem(createSimpleExpense(createSimpleDate(2019, Calendar.MAY, 6)));
        mItemHandler.insertItem(expectedItem);

        assertSame(3, mItemHandler.getItemCount());
        assertEquals(expectedItem, mItemHandler.getItem(2));
    }

    @Test
    public void testRemoveExistingItemShouldSucceed() {
        DateItem dateItem = new DateItem(createSimpleDate(2019, 5, 11));

        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(dateItem.getDate())));
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(dateItem.getDate())));

        assertEquals(3, mItemHandler.getItemCount());

        mItemHandler.removeItem(2);

        assertEquals(2, mItemHandler.getItemCount());
    }

    @Test
    public void testRemoveNotExistingItemShouldThrowIndexOutOfBoundException() {
        assertEquals(0, mItemHandler.getItemCount());

        try {
            mItemHandler.removeItem(1337);
            fail("Could remove Item at invalid position");

        } catch (IndexOutOfBoundsException e) {

            assertEquals("Could not find Item at position 1337", e.getMessage());
        }
    }

    @Test
    public void testRemoveLastItemShouldDeleteDateItem() {
        mItemHandler.insertItem(new ExpenseItem(createSimpleExpense(createSimpleDate(2019, 0, 24))));

        assertEquals(2, mItemHandler.getItemCount());

        mItemHandler.removeItem(1);

        assertEquals(0, mItemHandler.getItemCount());
    }

    private void assertSameDate(IRecyclerItem cal1, IRecyclerItem cal2) {
        boolean isSameDate = cal1.getDate().get(Calendar.YEAR) == cal2.getDate().get(Calendar.YEAR) &&
                cal1.getDate().get(Calendar.DAY_OF_YEAR) == cal2.getDate().get(Calendar.DAY_OF_YEAR);

        if (!isSameDate) {
            fail(String.format(
                    "Date %s does not equals Date %s",
                    CalendarUtils.formatHumanReadable(cal1.getDate()),
                    CalendarUtils.formatHumanReadable(cal2.getDate())
            ));
        }
    }

    private void fillHandlerWithItems(List<Calendar> itemDates) {
        for (Calendar itemDate : itemDates) {
            mItemHandler.insertItem(
                    new ExpenseItem(createSimpleExpense(itemDate))
            );
        }
    }
}
