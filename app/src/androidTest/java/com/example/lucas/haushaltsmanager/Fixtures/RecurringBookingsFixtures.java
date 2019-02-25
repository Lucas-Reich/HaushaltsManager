package com.example.lucas.haushaltsmanager.Fixtures;

import android.content.ContentValues;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingsFixtures implements IFixtures {
    private List<RecurringBooking> recurringBookings;

    private RecurringBookingsFixtures(List<RecurringBooking> recurringBookings) {
        this.recurringBookings = recurringBookings;
    }

    /**
     * Quelle: https://stackoverflow.com/a/52192348
     *
     * @param is Stream which contains a open connection to fixtures file
     */
    public static RecurringBookingsFixtures create(InputStream is) {
        List<RecurringBooking> recurringBookings = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonArray array = jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8))
                .getAsJsonObject()
                .getAsJsonArray("recurringBooking");

        for (JsonElement jsonRecurringBooking : array) {
            JsonObject jsonObject = (JsonObject) jsonRecurringBooking;

            JsonElement jsonStart = jsonObject.get("start");
            JsonElement jsonEnd = jsonObject.get("end");
            JsonObject jsonFrequency = jsonObject.getAsJsonObject("frequency");
            JsonObject jsonBooking = jsonObject.getAsJsonObject("booking");

            recurringBookings.add(RecurringBooking.create(
                    jsonToCalendar(jsonStart),
                    jsonToCalendar(jsonEnd),
                    jsonToFrequency(jsonFrequency),
                    jsonToBooking(jsonBooking)
            ));
        }

        return new RecurringBookingsFixtures(recurringBookings);
    }

    private static Frequency jsonToFrequency(JsonObject jsonObject) {
        return new Frequency(
                jsonObject.get("calendarField").getAsInt(),
                jsonObject.get("amount").getAsInt()
        );
    }

    private static Calendar jsonToCalendar(JsonElement jsonElement) {
        long timeInMillis = jsonElement.getAsLong();

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMillis);

        return date;
    }

    private static Booking jsonToBooking(JsonObject jsonObject) {
        // TODO: Wie kann ich das mit den Dependencies machen die ein Objekt eventuell hat?
        return null;
    }

    @Override
    public List<ContentValues> getContentValues() {
        List<ContentValues> values = new ArrayList<>();

        for (RecurringBooking recurringBooking : recurringBookings) {
            values.add(bind(recurringBooking));
        }

        return values;
    }

    private ContentValues bind(RecurringBooking recurringBooking) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBooking.getBooking().getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE, recurringBooking.getExecutionDate().getTimeInMillis());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD, recurringBooking.getFrequency().getCalendarField());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT, recurringBooking.getFrequency().getAmount());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, recurringBooking.getEnd().getTimeInMillis());

        return values;
    }
}
