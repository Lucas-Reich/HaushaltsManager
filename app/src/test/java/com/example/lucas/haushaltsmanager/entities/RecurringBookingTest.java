package com.example.lucas.haushaltsmanager.entities;


import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RecurringBookingTest {
    /**
     * 01.01.2018 + 1 Day = 02.01.2018
     */
    @Test
    public void testGetNextOccurrenceWithDay1() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.DATE, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(2, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 31.01.2018 + 1 Day = 01.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithDay2() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.DATE, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(1, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 + 1 Week = 08.01.2018
     */
    @Test
    public void testGetNextOccurrenceWithWeek1() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.WEEK_OF_YEAR, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(8, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 28.01.2018 + 1 Week = 04.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithWeek2() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(28, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.WEEK_OF_YEAR, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(4, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 + 1 Month = 01.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithMonth1() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(1, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * Der 31.01.2018 + 1 Month = 28.02.2018
     */
    @Test
    public void testGetNextOccurrenceWithMonth2() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(28, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2018, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 31.01.2018 + 1 Year = 31.01.2019
     */
    @Test
    public void testGetNextOccurrenceWithYear1() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(31, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.YEAR, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(31, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2019, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 29.02.2016 + 1 Year = 28.02.2017
     */
    @Test
    public void testGetNextOccurrenceWithYear2() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(29, Calendar.FEBRUARY, 2016),
                getDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.YEAR, 1)
        );

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);

        assertEquals(28, nextRecurringBooking.getDate().get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, nextRecurringBooking.getDate().get(Calendar.MONTH));
        assertEquals(2017, nextRecurringBooking.getDate().get(Calendar.YEAR));
    }

    /**
     * 01.01.2018 - 02.01.2019 = 13 RecurringBookings
     */
    @Test
    public void testGetCorrectRecurringBookingCount1() {
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(2, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1)
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
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(1, Calendar.JANUARY, 2019),
                new Frequency(Calendar.MONTH, 1)
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
        RecurringBooking recurringBooking = createRecurringBooking(
                getDate(1, Calendar.JANUARY, 2018),
                getDate(31, Calendar.DECEMBER, 2018),
                new Frequency(Calendar.MONTH, 1)
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

        RecurringBooking recurringBooking = createRecurringBooking(
                date,
                getDate(1, Calendar.JANUARY, 3000),
                new Frequency(Calendar.MONTH, 1)
        );

        Delay delay = recurringBooking.getDelayUntilNextExecution();

        assertEquals(fiveDaysInMillis, delay.getDuration(), 10F);
        assertEquals(TimeUnit.MILLISECONDS, delay.getTimeUnit());
    }

    @Test
    public void testGetInitialDelay2() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        RecurringBooking recurringBooking = createRecurringBooking(
                yesterday,
                getDate(1, Calendar.JANUARY, 3000),
                new Frequency(Calendar.MONTH, 1)
        );

        long expectedTimeBetween = getTimeBetweenNowAndNextRecurringBooking(recurringBooking);
        Delay delay = recurringBooking.getDelayUntilNextExecution();

        assertEquals(expectedTimeBetween, delay.getDuration(), 10F);
        assertEquals(TimeUnit.MILLISECONDS, delay.getTimeUnit());
    }

    private RecurringBooking createRecurringBooking(Calendar from, Calendar to, Frequency frequency) {
        Booking booking = getBooking();

        return new RecurringBooking(
                from,
                to,
                frequency,
                booking.getTitle(),
                booking.getPrice(),
                booking.getCategoryId(),
                booking.getAccountId()
        );
    }

    private long getTimeBetweenNowAndNextRecurringBooking(RecurringBooking recurringBooking) {
        Calendar now = Calendar.getInstance();

        return RecurringBooking.createNextRecurringBooking(recurringBooking).getDate().getTimeInMillis() - now.getTimeInMillis();
    }

    private Booking getBooking() {
        return new Booking(
                UUID.randomUUID(),
                "Ausgabe",
                new Price(-150),
                getDate(1, Calendar.JANUARY, 2019),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private Calendar getDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 12, 0, 0);

        return date;
    }
}
