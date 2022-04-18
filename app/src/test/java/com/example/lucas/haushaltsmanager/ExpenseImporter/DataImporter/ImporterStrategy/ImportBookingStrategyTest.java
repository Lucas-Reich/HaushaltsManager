package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImporterStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.AccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.CategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.BookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ImportBookingStrategyTest {
    private static final IDelimiter DEFAULT_DELIMITER = new Comma();

    private ISaver mockSaver;
    private ImportBookingStrategy strategy;

    @Before
    public void setUp() {
        mockSaver = mock(ISaver.class);

        strategy = new ImportBookingStrategy(
                new BookingParser(new PriceParser(new AbsDoubleParser(), new NumericPriceTypeParser()), new DateParser()),
                new AccountParser(),
                new CategoryParser(),
                mockSaver
        );
    }

    @Test
    public void hasExpectedRequiredFields() {
        // Act
        List<IRequiredField> requiredFields = strategy.getRequiredFields();

        // Assert
        assertEquals(6, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof BookingTitle);
        assertTrue(requiredFields.get(1) instanceof PriceValue);
        assertTrue(requiredFields.get(2) instanceof PriceType);
        assertTrue(requiredFields.get(3) instanceof Date);
        assertTrue(requiredFields.get(4) instanceof AccountTitle);
        assertTrue(requiredFields.get(5) instanceof CategoryTitle);
    }

    @Test
    public void parsesLineAndPersist() {
        // Arrange
        Line line = buildLine("BookingTitle", "100", "1", "Category", "01.01.2019", "Bank Account");

        // Act
        strategy.handle(line, getDefaultMappingList());

        // Assert
        verify(mockSaver, times(1)).persist(
                any(Booking.class),
                any(Account.class),
                any(Category.class)
        );
    }

    @Test
    public void throwsExceptionOnParsingError() {
        Line lineWithEmptyBookingTitle = buildLine("", "100", "1", "Category", "01.01.2019", "Bank Account");

        try {
            strategy.handle(lineWithEmptyBookingTitle, getDefaultMappingList());

            fail("Expected 'InvalidInputException' wasn't thrown");
        } catch (InvalidInputException e) {
            assertEquals("Could not create 'Booking' from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionOnMissingMapping() {
        try {
            strategy.handle(buildLine(""), new MappingList());

            fail("Expected 'NoMappingFoundException' wasn't thrown");
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'AccountTitle'.", e.getMessage());
        }
    }

    @Test
    public void abortWillRevertDatabaseChanges() {
        // Act
        strategy.abort();

        // Assert
        verify(mockSaver, times(1)).revert();
    }

    @Test
    public void finishWillReleaseResources() {
        // Act
        strategy.finish();

        // Assert
        verify(mockSaver, times(1)).finish();
    }

    private Line buildLine(String... input) {
        return new Line(
                String.join(DEFAULT_DELIMITER.getDelimiter(), input),
                DEFAULT_DELIMITER
        );
    }

    private MappingList getDefaultMappingList() {
        return new MappingList() {{
            addMapping(BookingParser.BOOKING_TITLE_KEY, 0);
            addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 1);
            addMapping(NumericPriceTypeParser.PRICE_TYPE_KEY, 2);
            addMapping(CategoryParser.CATEGORY_TITLE_KEY, 3);
            addMapping(DateParser.BOOKING_DATE_KEY, 4);
            addMapping(AccountParser.ACCOUNT_TITLE_KEY, 5);
        }};
    }
}
