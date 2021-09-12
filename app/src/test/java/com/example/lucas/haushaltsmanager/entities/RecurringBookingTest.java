package com.example.lucas.haushaltsmanager.entities;


import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class RecurringBookingTest {
    /**
     * 01.01.2018 + 1 Day = 02.01.2018
     */
    @Test
    public void testGetNextOccurrenceWithDay1() {
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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
        RecurringBooking recurringBooking = new RecurringBooking(
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

        RecurringBooking recurringBooking = new RecurringBooking(
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

        RecurringBooking recurringBooking = new RecurringBooking(
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

    private IBooking getBooking() {
        return new Booking(
                UUID.randomUUID(),
                "Ausgabe",
                new Price(150, true),
                getDate(1, Calendar.JANUARY, 2019),
                new Category("Kategorie", Color.black(), ExpenseType.Companion.expense()),
                "",
                UUID.randomUUID(),
                Booking.EXPENSE_TYPES.NORMAL_EXPENSE
        );
    }

    private Calendar getDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 12, 0, 0);

        return date;
    }
}
