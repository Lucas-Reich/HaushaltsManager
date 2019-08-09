package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.text.ParseException;
import java.util.Calendar;

public class DateParser implements IObjectParser<Calendar> {
    public static final String BOOKING_DATE_KEY = "booking_date";

    public DateParser() {
        // TODO: Hier könnte ich das format injecten, sodass dieses nicht jedes mal neu herausgefunden werden muss
    }

    public Calendar parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String stringifiedDate = line.getAsString(mapping.getMappingForKey(BOOKING_DATE_KEY));

        assertNotEmpty(stringifiedDate);

        return buildDate(stringifiedDate);
    }

    private Calendar buildDate(String date) throws InvalidInputException {
        String format = CalendarUtils.determineDateFormat(date);

        try {
            return CalendarUtils.fromString(date, format);
        } catch (ParseException | NullPointerException e) {
            throw InvalidInputException.invalidDateFormat(date);
        }
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Calendar.class);
    }
}
