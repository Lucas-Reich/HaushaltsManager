package com.example.lucas.haushaltsmanager;

import java.util.ArrayList;
import java.util.List;

public class WeekdayUtils {
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";
    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";

    public enum WEEKDAYS {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public static String getWeekday(int weekday) {
        //todo ist der wert größer als 6 oder kleiner als null wird eine exception ausgelöst
        return formatWeekday(WEEKDAYS.values()[weekday].name());
    }

    private static String formatWeekday(String stringDay) {
        stringDay = stringDay.toLowerCase();
        stringDay = stringDay.substring(0, 1).toUpperCase() + stringDay.substring(1);

        return stringDay;
    }

    public static int getWeekday(String weekday) {
        return WEEKDAYS.valueOf(weekday.toUpperCase()).ordinal();
    }

    public static String[] getWeekdays() {
        String[] weekdays = new String[7];

        for (int i = 0; i < 7; i++) {
            weekdays[i] = formatWeekday(WEEKDAYS.values()[i].name());
        }

        return weekdays;
    }
}
