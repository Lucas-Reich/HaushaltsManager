package com.example.lucas.haushaltsmanager;

import java.util.Locale;

public class Time {

    private int mHour;
    private int mMinute;

    public Time(int hour, int minute) {

        mHour = hour;
        mMinute = minute;
    }

    public static Time fromString(String stringTime) {
        int hour = Integer.parseInt(stringTime.substring(0, 2));
        int minute = Integer.parseInt(stringTime.substring(3, 5));

        return new Time(hour, minute);
    }

    public String getTime() {
        return String.format(Locale.US, "%02d:%02d", mHour, mMinute);
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }
}
