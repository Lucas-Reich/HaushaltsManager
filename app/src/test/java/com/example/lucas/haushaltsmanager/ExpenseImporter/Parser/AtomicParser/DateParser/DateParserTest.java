package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DateParserTest {
    private DateParser parser;

    @Before
    public void setUp() {
        parser = new DateParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        assertEquals(1, requiredFields.size());
        assertTrue((requiredFields.get(0) instanceof Date));
    }

    @Test
    public void parserCreateDate() {
        // SetUp
        String expectedDate = "06.09.2019";
        Line line = buildLine(expectedDate);

        // Act
        Calendar date = parser.parse(line, createDateMappingList());

        // Assert
        assertEquals(expectedDate, CalendarUtils.formatHumanReadable(date));
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'Date'.", e.getMessage());
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
        mappingList.addMapping(DateParser.BOOKING_DATE_KEY, 0);

        return mappingList;
    }

    private Line buildLine(String... input) {
        IDelimiter delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}