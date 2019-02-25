package com.example.lucas.haushaltsmanager.Entities;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class RecurringBooking {
    private final long mIndex;

    private Calendar executionDate, endDate;
    private Frequency frequency;
    private Booking templateBooking;

    private RecurringBooking(long index, Calendar executionDate, Calendar end, Frequency frequency, Booking booking) {
        mIndex = index;

        setBooking(booking);
        setExecutionDate(executionDate);
        setEnd(end);
        setFrequency(frequency);
    }

    public static RecurringBooking load(long id, Calendar executionDate, Calendar end, Frequency frequency, Booking booking) {
        return new RecurringBooking(
                id,
                executionDate,
                end,
                frequency,
                booking
        );
    }

    public static RecurringBooking create(Calendar executionDate, Calendar end, Frequency frequency, Booking booking) {
        return new RecurringBooking(
                ExpensesDbHelper.INVALID_INDEX,
                executionDate,
                end,
                frequency,
                booking
        );
    }

    /**
     * Method which creates the next RecurringBooking based on the Frequency information within it.
     *
     * @param recurringBooking Base RecurringBooking
     * @return RecurringBooking with next schedule date. NULL if time frame ended.
     */
    @Nullable
    public static RecurringBooking createNextRecurringBooking(RecurringBooking recurringBooking) {
        Calendar nextOccurrence = recurringBooking.getNextOccurrence();
        if (nextOccurrence.after(recurringBooking.getEnd())) {
            return null;
        }

        return new RecurringBooking(
                recurringBooking.getIndex(),
                nextOccurrence,
                recurringBooking.getEnd(),
                recurringBooking.getFrequency(),
                ExpenseObject.copy(recurringBooking.getBooking())
        );
    }

    public long getIndex() {
        return mIndex;
    }

    public ExpenseObject getBooking() {
        return (ExpenseObject) templateBooking;
    }

    private void setBooking(Booking booking) {
        templateBooking = booking;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Calendar getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Calendar executionDate) {
        this.executionDate = executionDate;
        ((ExpenseObject) templateBooking).setDateTime(executionDate);
    }

    public Calendar getEnd() {
        return endDate;
    }

    public void setEnd(Calendar end) {
        // TODO: Solle ich hier noch einen check einführen, welcher garantiert, dass das EndDatum auch nach dem start ist?
        endDate = end;
    }

    public Delay getDelayUntilNextExecution() {
        long timeBetween = getTimeBetweenNowAnd(executionDate);
        if (timeBetween < 0) {
            timeBetween = getTimeBetweenNowAnd(getNextOccurrence());
        }

        return new Delay(
                TimeUnit.MILLISECONDS,
                timeBetween
        );
    }

    private Calendar getNextOccurrence() {
        Calendar nextOccurrence = (Calendar) executionDate.clone();

        increaseByFrequency(nextOccurrence, frequency);

        return nextOccurrence;
    }

    private long getTimeBetweenNowAnd(Calendar otherDate) {
        Calendar now = Calendar.getInstance();

        return otherDate.getTimeInMillis() - now.getTimeInMillis();
    }

    private void increaseByFrequency(Calendar date, Frequency frequency) {
        date.add(frequency.getCalendarField(), frequency.getAmount());
    }
}
