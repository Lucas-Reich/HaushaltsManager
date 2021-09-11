package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Type;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BookingParserTest {
    private BookingParser parser;

    @Before
    public void setUp() {
        parser = new BookingParser(
                new PriceParser(),
                new CategoryParser(),
                new DateParser()
        );
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        assertEquals(5, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof Title);
        assertTrue(requiredFields.get(1) instanceof Value);
        assertTrue(requiredFields.get(2) instanceof Type);
        assertTrue(requiredFields.get(3) instanceof com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.Title);
        assertTrue(requiredFields.get(4) instanceof Date);
    }

    @Test
    public void parserCreatesBooking() {
        // SetUp
        String expectedBookingTitle = "any string";
        Line line = buildLine(expectedBookingTitle, "-100", "06.09.2019", "any string");

        // Act
        Booking booking = parser.parse(line, createBookingMappingList());

        // Assert
        assertEquals(expectedBookingTitle, booking.getTitle());
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string", "any string", "", "");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'Title'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyBookingTitle() {
        Line line = buildLine("", "", "", "");

        try {
            parser.parse(line, createBookingMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create ExpenseObject from 'empty string', invalid input.", e.getMessage());
        }
    }

    private MappingList createBookingMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(BookingParser.BOOKING_TITLE_KEY, 0);
        mappingList.addMapping(PriceParser.PRICE_VALUE_KEY, 1);
        mappingList.addMapping(PriceParser.PRICE_TYPE_KEY, 1);
        mappingList.addMapping(DateParser.BOOKING_DATE_KEY, 2);
        mappingList.addMapping(CategoryParser.CATEGORY_TITLE_KEY, 3);

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