package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingDate;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;

public class DateParserTest {
    private DateParser parser;

    @Before
    public void setUp() {
        parser = new DateParser();
    }

    @Test
    public void parserCreateDate() {
        // SetUp
        String expectedCategoryTitle = "06.09.2019";
        Line line = buildLine(expectedCategoryTitle);

        // Act
        Calendar date = parser.parse(line, createDateMappingList());

        // Assert
        assertEquals(expectedCategoryTitle, CalendarUtils.formatHumanReadable(date));
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'booking_date'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyBookingDate() {
        Line line = buildLine("");

        try {
            parser.parse(line, createDateMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Calendar from 'empty string', invalid input.", e.getMessage());
        }
    }

    private MappingList createDateMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(new ImportableBookingDate(DateParser.BOOKING_DATE_KEY));

        return mappingList;
    }

    private Line buildLine(String... input) {
        DelimiterInterface delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), DateParser.BOOKING_DATE_KEY),
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}