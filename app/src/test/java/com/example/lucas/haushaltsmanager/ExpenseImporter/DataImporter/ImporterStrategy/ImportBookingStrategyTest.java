package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImporterStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Type;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ImportBookingStrategyTest {
    private ISaver mockSaver;

    private ImportBookingStrategy strategy;

    @Before
    public void setUp() {
        mockSaver = mock(ISaver.class);

        strategy = new ImportBookingStrategy(
                new BookingParser(new PriceParser(), new CategoryParser(), new DateParser()),
                new AccountParser(),
                mockSaver
        );
    }

    @Test
    public void hasExpectedRequiredFields() {
        // Act
        List<IRequiredField> requiredFields = strategy.getRequiredFields();


        // Assert
        assertEquals(6, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof Title);
        assertTrue(requiredFields.get(1) instanceof Value);
        assertTrue(requiredFields.get(2) instanceof Type);
        assertTrue(requiredFields.get(3) instanceof com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.Title);
        assertTrue(requiredFields.get(4) instanceof Date);
        assertTrue(requiredFields.get(5) instanceof com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.Title);
    }

    @Test
    public void throwsExceptionOnParsingError() {
        Line lineWithEmptyBookingTitle = new Line(",100,1,Kategorie,01.01.2019,Konto", new Comma());

        try {
            strategy.handle(lineWithEmptyBookingTitle, getDefaultMappingList());

            fail("Expected Exception wasn't thrown");
        } catch (InvalidInputException e) {
            assertEquals("Could not create ExpenseObject from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionOnMissingMapping() {
        try {
            strategy.handle(new Line("", new Comma()), new MappingList());

            fail("Expected Exception wasn't thrown");
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'Title'.", e.getMessage());
        }
    }

    @Test
    public void parsesLineAndPersist() {
        // Set Up
        Line line = new Line("BookingTitle,100,1,Kategorie,01.01.2019,Konto", new Comma());


        // Act
        strategy.handle(line, getDefaultMappingList());


        // Assert
        verify(mockSaver, times(1))
                .persist(any(Booking.class), any(Account.class));
    }

    @Test
    public void abortWillRevertDatabaseChanges() {
        strategy.abort();

        verify(mockSaver, times(1)).revert();
    }

    @Test
    public void finishWillReleaseResources() {
        strategy.finish();

        verify(mockSaver, times(1)).finish();
    }

    private MappingList getDefaultMappingList() {
        return new MappingList() {{
            addMapping(BookingParser.BOOKING_TITLE_KEY, 0);
            addMapping(PriceParser.PRICE_VALUE_KEY, 1);
            addMapping(PriceParser.PRICE_TYPE_KEY, 2);
            addMapping(CategoryParser.CATEGORY_TITLE_KEY, 3);
            addMapping(DateParser.BOOKING_DATE_KEY, 4);
            addMapping(AccountParser.ACCOUNT_TITLE_KEY, 5);
        }};
    }
}
