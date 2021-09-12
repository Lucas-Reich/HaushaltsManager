package com.example.lucas.haushaltsmanager.entities;

public class Frequency {
    private final int mCalendarField;
    private final int mAmount;

    public Frequency(int calendarField, int amount) {
        mCalendarField = calendarField;
        mAmount = amount;
    }

    public int getCalendarField() {
        return mCalendarField;
    }

    public int getAmount() {
        return mAmount;
    }
}
