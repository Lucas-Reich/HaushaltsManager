package com.example.lucas.haushaltsmanager.Entities;

import java.util.Calendar;

public class RecurringBooking {
    private Calendar mStart, mEnd;
    /**
     * Frequency in hours
     */
    private int mFrequency;
    private long mIndex;
    private ExpenseObject mExpense;//TODO kann ich die Buchung nur als referenz speichern (nur als id)?

    public RecurringBooking(
            long index,
            Calendar start,
            Calendar end,
            int frequency,
            ExpenseObject expense
    ) {
        mStart = start;
        mEnd = end;
        mFrequency = frequency;
        mIndex = index;
        mExpense = expense;
    }

    public RecurringBooking(Calendar start, Calendar end, int frequency, ExpenseObject expense) {
        this(-1, start, end, frequency, expense);
    }

    public long getIndex() {
        return mIndex;
    }

    public ExpenseObject getExpense() {
        return mExpense;
    }

    public Calendar getStart() {
        return mStart;
    }

    public Calendar getEnd() {
        return mEnd;
    }

    public int getFrequency() {
        return mFrequency;
    }

    public boolean occursInRange(Calendar start, Calendar end) {
        if (start.after(mEnd) || end.before(mStart))
            return false;

        Calendar temp = mStart;
        while (temp.before(end)) {
            if (isDateInRange(temp, start, end))
                return true;

            temp.add(Calendar.HOUR, getFrequency());
        }

        return false;
    }

    public Calendar getNextOccurrenceAfter(Calendar date) {
        Calendar temp = Calendar.getInstance();
        while (temp.before(mEnd)) {
            if (temp.after(date))
                return temp;

            temp.add(Calendar.HOUR, mFrequency);
        }

        return null;// IMPROVEMENT: Kann ich außer NULL auch was anderes zurückgeben?
    }

    private boolean isDateInRange(Calendar date, Calendar start, Calendar end) {
        return date.after(start) && date.before(end);
    }
}
