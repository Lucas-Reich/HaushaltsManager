package com.example.lucas.haushaltsmanager.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

@Entity(tableName = "recurring_bookings")
public class RecurringBooking {
    @PrimaryKey
    private final UUID id;
    @ColumnInfo(name = "end_date")
    private final Calendar endDate;
    private final Frequency frequency;

    private final String title;
    private final Price price;
    private final Calendar date;
    @ColumnInfo(name = "category_id")
    private final UUID categoryId;
    @ColumnInfo(name = "account_id")
    private final UUID accountId;

    public RecurringBooking(
            @NonNull UUID id,
            @NonNull Calendar date,
            @NonNull Calendar end,
            @NonNull Frequency frequency,
            @NonNull String title,
            @NonNull Price price,
            @NonNull UUID categoryId,
            @NonNull UUID accountId
    ) {
        this.id = id;
        this.date = date;
        this.endDate = end;
        this.frequency = frequency;

        this.title = title;
        this.price = price;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public RecurringBooking(
            @NonNull Calendar start,
            @NonNull Calendar end,
            @NonNull Frequency frequency,
            @NonNull String title,
            @NonNull Price price,
            @NonNull UUID categoryId,
            @NonNull UUID accountId
    ) {
        this(
                UUID.randomUUID(),
                start,
                end,
                frequency,
                title,
                price,
                categoryId,
                accountId
        );
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
                recurringBooking.title,
                recurringBooking.price,
                recurringBooking.categoryId,
                recurringBooking.accountId
        );
    }

    public UUID getId() {
        return id;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public Calendar getDate() {
        return date;
    }

    public Calendar getEnd() {
        return endDate;
    }

    public Delay getDelayUntilNextExecution() {
        long timeBetween = getTimeBetweenNowAnd(date);
        if (timeBetween < 0) {
            timeBetween = getTimeBetweenNowAnd(getNextOccurrence());
        }

        return new Delay(
                TimeUnit.MILLISECONDS,
                timeBetween
        );
    }

    public String getTitle() {
        return title;
    }

    public Price getPrice() {
        return price;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    private Calendar getNextOccurrence() {
        Calendar nextOccurrence = (Calendar) date.clone();

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
