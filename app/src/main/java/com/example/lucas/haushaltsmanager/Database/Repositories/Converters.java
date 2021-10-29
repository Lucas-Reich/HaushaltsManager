package com.example.lucas.haushaltsmanager.Database.Repositories;

import androidx.room.TypeConverter;

import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Frequency;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import java.util.Calendar;
import java.util.UUID;

public class Converters {
    @TypeConverter
    public static UUID uuidFromString(String uuid) {
        return UUID.fromString(uuid);
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    @TypeConverter
    public static Price priceFromDouble(double price) {
        return new Price(price);
    }

    @TypeConverter
    public static double priceToDouble(Price price) {
        return price.getPrice();
    }

    @TypeConverter
    public static Color colorFromString(String color) {
        return new Color(color);
    }

    @TypeConverter
    public static String colorToString(Color color) {
        return color.getColor();
    }

    @TypeConverter
    public static ExpenseType expenseTypeFromBool(boolean expenseType) {
        return ExpenseType.Companion.load(expenseType);
    }

    @TypeConverter
    public static boolean expenseTypeToBool(ExpenseType expenseType) {
        return expenseType.getType();
    }

    @TypeConverter
    public static Calendar calendarFromLong(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return calendar;
    }

    @TypeConverter
    public static long calendarToLong(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public static Frequency frequencyFromString(String frequency) {
        String[] rawInput = frequency.split(";");

        return new Frequency(
                Integer.parseInt(rawInput[0]),
                Integer.parseInt(rawInput[1])
        );
    }

    @TypeConverter
    public static String frequencyToString(Frequency frequency) {
        return "" + frequency.getCalendarField() + ";" + frequency.getAmount();
    }
}
