package com.example.lucas.haushaltsmanager.Entities;

import java.util.Calendar;
import java.util.Locale;

public class Time {
    private int hour;
    private int minute;

    public Time(int hour, int minute) {

        this.hour = hour;
        this.minute = minute;
    }

    public static Time fromString(String stringTime) {
        int hour = Integer.parseInt(stringTime.substring(0, 2));
        int minute = Integer.parseInt(stringTime.substring(3, 5));

        return new Time(hour, minute);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%02d:%02d", hour, minute);
    }

    public long toMillis() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);

        return time.getTimeInMillis();
    }
}
