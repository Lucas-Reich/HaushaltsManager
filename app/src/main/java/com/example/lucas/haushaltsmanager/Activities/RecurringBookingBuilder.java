package com.example.lucas.haushaltsmanager.Activities;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Frequency;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;

import java.util.Calendar;

public class RecurringBookingBuilder {
    private Calendar start;
    private Calendar end;
    private Frequency frequency;
    private Booking booking;

    public void setStart(Calendar start) {
        this.start = start;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public RecurringBooking build() throws RuntimeException {
        if (!recurringBookingCanBeCreated()) {
            throw new RuntimeException("Cannot create RecurringBooking");
        }

        return new RecurringBooking(
                start,
                end,
                frequency,
                booking.getTitle(),
                booking.getPrice(),
                booking.getCategory().getId(),
                booking.getAccountId()
        );
    }

    public boolean recurringBookingCanBeCreated() {
        if (null == start) {
            return false;
        }

        if (null == end) {
            return false;
        }

        if (null == frequency) {
            return false;
        }

        if (null == booking) {
            return false;
        }

        return true;
    }
}
