package com.example.lucas.haushaltsmanager.Entities;


import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecurringBookingTest {
    /**
     * 01.01.2018 + 1 Day = 02.01.2018
     */
    @Test
    public void testGetNextOccurrenceWithDay1() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.DATE, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(2, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 31.01.2018 + 1 Day = 01.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithDay2() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.DATE, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(1, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 + 1 Week = 08.01.2018
     */
    @Test
    public void testGetNextOccurrenceWithWeek1() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.WEEK_OF_YEAR, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(8, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 28.01.2018 + 1 Week = 04.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithWeek2() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(28, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.WEEK_OF_YEAR, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(4, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 + 1 Month = 01.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithMonth1() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(1, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * Der 31.01.2018 + 1 Month = 28.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithMonth2() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(28, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 31.01.2018 + 1 Year = 31.01.2019
     */
    @Test
    public void testGetNextOccurrenceWithYear1() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.YEAR, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(31, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2019, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 29.02.2016 + 1 Year = 28.02.2017
     */
    @Test
    public void testGetNextOccurrenceWithYear2() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(29, Calendar.FEBRUARY, 2016),
                getDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.YEAR, 1),
                getBooking()
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(28, nextRecurringBooking.getExecutionDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getExecutionDate().get(Calendar.MONTH));
        assertEquals(2017, nextRecurringBooking.getExecutionDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 - 02.01.2019 = 13 RecurringBookings
     */
    @Test
    public void testGetCorrectRecurringBookingCount1() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(2, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        List<RecurringBooking> recurringBookings = new ArrayList<>();

        do {
            recurringBookings.add(recurringBooking);

            recurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);
        } while (recurringBooking != null);

        assertEquals(13, recurringBookings.size());
    }

    /**
     * 01.01.2018 - 01.01.2019 = 13 RecurringBookings
     */
    @Test
    public void testGetCorrectRecurringBookingCount2() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        List<RecurringBooking> recurringBookings = new ArrayList<>();

        do {
            recurringBookings.add(recurringBooking);

            recurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);
        } while (recurringBooking != null);

        assertEquals(13, recurringBookings.size());
    }

    /**
     * 01.01.2018 - 31.12.2018 = 12 RecurringBookings
     */
    @Test
    public void testGetCorrectRecurringBookingCount3() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(31, Calendar.DECEMBER, 2018),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        List<RecurringBooking> recurringBookings = new ArrayList<>();

        do {
            recurringBookings.add(recurringBooking);

            recurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);
        } while (recurringBooking != null);

        assertEquals(12, recurringBookings.size());
    }

    @Test
    public void testGetInitialDelay1() {
        long fiveDaysInMillis = 432000000L;

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 5);

        RecurringBooking recurringBooking = RecurringBooking.create(
                date,
                getDate(1, Calendar.JANUARY, 3000),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        Delay delay = recurringBooking.getDelayUntilNextExecution();

        assertEquals(fiveDaysInMillis, delay.getDuration(), 10F);
        assertEquals(TimeUnit.MILLISECONDS, delay.getTimeUnit());
    }

    @Test
    public void testGetInitialDelay2() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        RecurringBooking recurringBooking = RecurringBooking.create(
                yesterday,
                getDate(1, Calendar.JANUARY, 3000),
                new Frequency(Calendar.MONTH, 1),
                getBooking()
        );

        long expectedTimeBetween = getTimeBetweenNowAndNextRecurringBooking(recurringBooking);
        Delay delay = recurringBooking.getDelayUntilNextExecution();

        assertEquals(expectedTimeBetween, delay.getDuration(), 10F);
        assertEquals(TimeUnit.MILLISECONDS, delay.getTimeUnit());
    }

    private long getTimeBetweenNowAndNextRecurringBooking(RecurringBooking recurringBooking) {
        Calendar now = Calendar.getInstance();

        return RecurringBooking.createNextRecurringBooking(recurringBooking).getExecutionDate().getTimeInMillis() - now.getTimeInMillis();
    }

    private Booking getBooking() {
        return new ExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                "Ausgabe",
                150,
                getDate(1, Calendar.JANUARY, 2019),
                true,
                new Category("Kategorie", "#000000", true, new ArrayList<Category>()),
                "",
                ExpensesDbHelper.INVALID_INDEX,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                new Currency("Euro", "EUR", "€")
        );
    }

    private Calendar getDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 12, 0, 0);

        return date;
    }
}
