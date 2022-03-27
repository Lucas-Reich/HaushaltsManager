package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.BookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BookingParserTest {
    private BookingParser parser;

    @Before
    public void setUp() {
        parser = new BookingParser(
                new PriceParser(new AbsDoubleParser(), new NumericPriceTypeParser()),
                new DateParser()
        );
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        // Act
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        // Assert
        assertEquals(4, requiredFields.size());

        assertTrue(requiredFields.get(0) instanceof BookingTitle);
        assertTrue(requiredFields.get(1) instanceof PriceValue);
        assertTrue(requiredFields.get(2) instanceof PriceType);
        assertTrue(requiredFields.get(3) instanceof Date);
    }

    @Test
    public void parserCreatesBooking() {
        // SetUp
        String expectedBookingTitle = "any string";
        String expectedDate = "06.09.2019";
        double expectedValue = -100;
        Line line = buildLine(
                expectedBookingTitle,
                String.valueOf(expectedValue),
                expectedDate
        );

        // Act
        Booking booking = parser.parse(line, createMapping());

        // Assert
        assertEquals(expectedBookingTitle, booking.getTitle());
        assertEquals(expectedValue, booking.getPrice().getPrice(), 0);

        String actualDate = CalendarUtils.formatHumanReadable(booking.getDate());
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void parserThrowsExceptionForEmptyBookingTitle() {
        Line line = buildLine("", "", "");

        try {
            parser.parse(line, createMapping());
        } catch (InvalidInputException e) {
            assertEquals("Could not create 'Booking' from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any title", "100", "01.01.2020");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'BookingTitle'.", e.getMessage());
        }
    }

    private MappingList createMapping() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(BookingParser.BOOKING_TITLE_KEY, 0);
        mappingList.addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 1);
        mappingList.addMapping(NumericPriceTypeParser.PRICE_TYPE_KEY, 1);
        mappingList.addMapping(DateParser.BOOKING_DATE_KEY, 2);

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