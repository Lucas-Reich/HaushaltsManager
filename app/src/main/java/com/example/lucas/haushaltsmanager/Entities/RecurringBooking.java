package com.example.lucas.haushaltsmanager.Entities;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class RecurringBooking {
    private final UUID id;
    private Calendar start, endDate;
    private final Frequency frequency;
    private final Booking templateBooking;

    public RecurringBooking(
            @NonNull UUID id,
            @NonNull Calendar start,
            @NonNull Calendar end,
            @NonNull Frequency frequency,
            @NonNull Booking booking
    ) {
        this.id = id;
        this.templateBooking = booking;
        setExecutionDate(start);
        this.endDate = end;
        this.frequency = frequency;
    }

    public RecurringBooking(
            @NonNull Calendar start,
            @NonNull Calendar end,
            @NonNull Frequency frequency,
            @NonNull Booking booking
    ) {
        this(UUID.randomUUID(), start, end, frequency, booking);
    }

    @Nullable
    public static RecurringBooking createNextRecurringBooking(RecurringBooking recurringBooking) {
        Calendar start = recurringBooking.getNextOccurrence();
        if (start.after(recurringBooking.getEnd())) {
            return null;
        }

        return new RecurringBooking(
                start,
                recurringBooking.getEnd(),
                recurringBooking.getFrequency(),
                recurringBooking.getBooking()
        );
    }

    public UUID getId() {
        return id;
    }

    public ExpenseObject getBooking() {
        return (ExpenseObject) templateBooking;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public Calendar getExecutionDate() {
        return start;
    }

    public void setExecutionDate(Calendar executionDate) {
        this.start = executionDate;
        templateBooking.setDate(executionDate);
    }

    public Calendar getEnd() {
        return endDate;
    }

    public Delay getDelayUntilNextExecution() {
        long timeBetween = getTimeBetweenNowAnd(start);
        if (timeBetween < 0) {
            timeBetween = getTimeBetweenNowAnd(getNextOccurrence());
        }

        return new Delay(
                TimeUnit.MILLISECONDS,
                timeBetween
        );
    }

    private Calendar getNextOccurrence() {
        Calendar nextOccurrence = (Calendar) start.clone();

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
