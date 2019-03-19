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

    private static Locale getUserLocale() {
        return Locale.getDefault();
    }
}
