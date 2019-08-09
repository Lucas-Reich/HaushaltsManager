package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingDate;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableCategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.DateParser;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class BookingParserTest {
    private BookingParser parser;

    @Before
    public void setUp() {
        parser = new BookingParser(mock(Currency.class));
    }

    @Test
    public void parserCreatesBooking() {
        // SetUp
        String expectedBookingTitle = "any string";
        Line line = buildLine(expectedBookingTitle, "-100", "06.09.2019", "any string");

        // Act
        ExpenseObject booking = parser.parse(line, createBookingMappingList());

        // Assert
        assertEquals(expectedBookingTitle, booking.getTitle());
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string", "any string", "", "", "");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'booking_title'.", e.getMessage());
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
        mappingList.addMapping(new ImportableBookingTitle(BookingParser.BOOKING_TITLE_KEY));
        mappingList.addMapping(new ImportablePriceValue("price"));
        mappingList.addMapping(new ImportablePriceType("price"));
        mappingList.addMapping(new ImportableBookingDate(DateParser.BOOKING_DATE_KEY));
        mappingList.addMapping(new ImportableCategoryTitle(CategoryParser.CATEGORY_TITEL_KEY));

        return mappingList;
    }

    private Line buildLine(String... input) {
        DelimiterInterface delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), BookingParser.BOOKING_TITLE_KEY, "price", DateParser.BOOKING_DATE_KEY, CategoryParser.CATEGORY_TITEL_KEY),
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}