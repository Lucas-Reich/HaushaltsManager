package com.example.lucas.haushaltsmanager;

import android.content.Context;

public class WeekdayUtils {
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    private String[] mWeekdays;

    public WeekdayUtils(Context context) {

        mWeekdays = context.getResources().getStringArray(R.array.weekdays);
    }

    public String getWeekday(int weekdayIndex) {
        return mWeekdays[weekdayIndex];
    }

    public int getWeekdayIndex(String weekday) {
        for (int i = 0; i < mWeekdays.length; i++) {
            if (mWeekdays[i].equals(weekday))
                return i;
        }

        return -1;
    }

    public String[] getWeekdays() {
        return mWeekdays;
    }
}
