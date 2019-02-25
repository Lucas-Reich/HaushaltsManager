package com.example.lucas.haushaltsmanager.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    public static Calendar getFirstOfMonth(int year, int month) {
        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(year, month, 1);

        return firstOfMonth;
    }

    public static Calendar getLastOfMonth(int year, int month) {
        Calendar lastOfMonth = Calendar.getInstance();
        lastOfMonth.set(Calendar.YEAR, year);
        lastOfMonth.set(Calendar.MONTH, month);
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        return lastOfMonth;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String formatHumanReadable(Calendar date) {
        // TODO: Formatierung sollte basierend auf der Locale des Users sein
        // DateUtils.formatDateTime könnte man benutzen
        return new SimpleDateFormat("dd.MM.yyyy", getUserLocale()).format(date.getTime());
    }

    public static boolean areEqual(Calendar date1, Calendar date2) {
        return date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)
                && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                && date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR);
    }

    private static Locale getUserLocale() {
        return Locale.getDefault();
    }
}
