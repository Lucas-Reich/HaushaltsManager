package com.example.lucas.haushaltsmanager.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String formatHumanReadable(Calendar date) {
        // TODO: Formatierung sollte basierend auf der Locale des Users sein
        // DateUtils.formatDateTime könnte man benutzen
        return new SimpleDateFormat("dd.MM.yyyy", getUserLocale()).format(date.getTime());
    }

    public static boolean beforeByDate(Calendar one, Calendar two) {
        Calendar date1 = prepareDate(one);
        Calendar date2 = prepareDate(two);

        return date1.before(date2);
    }

    public static boolean afterByDate(Calendar one, Calendar two) {
        Calendar date1 = prepareDate(one);
        Calendar date2 = prepareDate(two);

        return date1.after(date2);
    }

    private static Calendar prepareDate(Calendar date) {
        Calendar preparedDate = Calendar.getInstance();

        preparedDate.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DATE),
                0,
                0,
                0
        );

        return preparedDate;
    }

    private static Locale getUserLocale() {
        return Locale.getDefault();
    }
}
