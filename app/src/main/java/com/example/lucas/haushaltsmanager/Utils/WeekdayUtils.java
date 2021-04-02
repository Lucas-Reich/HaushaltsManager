package com.example.lucas.haushaltsmanager.Utils;

import android.content.Context;

import com.example.lucas.haushaltsmanager.R;

public class WeekdayUtils {
    public static final int MONDAY = 0;

    private final String[] mWeekdays;

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
