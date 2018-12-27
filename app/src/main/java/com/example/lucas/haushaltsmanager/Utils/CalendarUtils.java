package com.example.lucas.haushaltsmanager.Utils;

import java.util.Calendar;

public class CalendarUtils {
    public static Calendar getFirstOfCurrentMonth() {
        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstOfMonth.set(Calendar.MINUTE, 0);
        firstOfMonth.set(Calendar.SECOND, 1);
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        return firstOfMonth;
    }

    public static Calendar getLastOfCurrentMonth() {
        int lastDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar lastOfMonth = Calendar.getInstance();
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastDayMonth);

        return lastOfMonth;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
