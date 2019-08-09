package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.AccountMappings.ImportableAccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingDate;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableCategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceValue;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ParserTest {
    @Test
    public void canBuildAccountForLine() {
        // SetUp
        Currency expectedCurrency = mock(Currency.class);
        IParser parser = new Parser(expectedCurrency, createAccountMappingList());


        // Act
        Account account = parser.parseAccount(
                new Line("account,amount", "Mein Konto Name,1500", new Comma())
        );


        // Assert
        assertEquals(-1L, account.getIndex());
        assertEquals("Mein Konto Name", account.getTitle());
        assertEquals(0, account.getBalance().getSignedValue(), 0);
        assertEquals(expectedCurrency, account.getBalance().getCurrency());
    }

    @Test
    public void throwsExceptionIfMappingForAccountIsNotDefined() {
        try {
            IParser parser = new Parser(mock(Currency.class), new MappingList());

            parser.parseAccount(new Line(
                    "account,amount", "any string, 77", new Comma()
            ));

            Assert.fail("Could build account from invalid line");
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'account_title'.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionIfAccountNameIsEmpty() {
        try {
            IParser parser = new Parser(mock(Currency.class), createAccountMappingList());

            parser.parseAccount(new Line(
                    "account,amount", ",1500", new Comma()
            ));

            Assert.fail("Could build Account from invalid Line");
        } catch (InvalidInputException e) {
            assertEquals("Could not create Account from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void canBuildBookingFromLine() {
        // SetUp
        Currency expectedCurrency = mock(Currency.class);
        IParser parser = new Parser(expectedCurrency, createBookingMappingList());


        // Act
        ExpenseObject booking = parser.parseBooking(new Line(
                "title,price_type,price,date,category",
                "Ich bin eine Buchung,0,500,26.08.2019 12:00:00,Ausgaben",
                new Comma())
        );


        // Assert
        assertEquals(ExpensesDbHelper.INVALID_INDEX, booking.getIndex());
        assertEquals(ExpensesDbHelper.INVALID_INDEX, booking.getAccountId());

        assertEquals("Ich bin eine Buchung", booking.getTitle());
        assertEquals(500D, booking.getPrice().getSignedValue(), 0);
        assertEquals("26.08.2019", CalendarUtils.formatHumanReadable(booking.getDate()));
        assertEquals("Ausgaben", booking.getCategory().getTitle());
        assertEquals(expectedCurrency, booking.getCurrency());
        assertEquals("", booking.getNotice());
    }

    // TODO: Sollte ich auch noch einen Test schreiben, der auf eine negative Ausgabe testet

    @Test
    public void throwsExceptionIfMappingForBookingIsNotDefined() {
        try {
            IParser parser = new Parser(mock(Currency.class), new MappingList());

            parser.parseBooking(new Line(
                    "title,price_type,price,date,category,notice",
                    "Ich bin eine Buchung,1,500,26.08.2019 12:00:00,Ausgaben,",
                    new Comma())
            );

            Assert.fail("Could build Booking from invalid Line.");
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'booking_title'.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionIfCategoryNameIsEmpty() {
        try {
            IParser parser = new Parser(mock(Currency.class), createBookingMappingList());

            parser.parseBooking(new Line(
                    "title,price_type,price,date,category",
                    "Ich bin eine Buchung,1,500,26.08.2019 12:00:00,",
                    new Comma()
            ));

            Assert.fail("Could build Booking from invalid Line");
        } catch (InvalidInputException e) {
            assertEquals("Could not create Category from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionIfDateStringIsEmpty() {
        IParser parser = new Parser(mock(Currency.class), createBookingMappingList());

        try {
            parser.parseBooking(new Line(
                    "title,price_type,price,date,category",
                    "Ich bin eine Buchung,1,500,,Ausgaben",
                    new Comma()
            ));

            Assert.fail("Could build Booking from invalid Line");
        } catch (InvalidInputException e) {
            assertEquals("Could not create Calendar from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionIfDateFormatForBookingIsNotSupported() {
        // SetUp
        IParser parser = new Parser(mock(Currency.class), createBookingMappingList());

        // Act
        try {
            parser.parseBooking(new Line(
                    "title,price_type,price,date,category",
                    "Ich bin eine Buchung,1,500,invalid_date_string,Ausgaben",
                    new Comma())
            );

            Assert.fail("Could create Booking from invalid date String");
        } catch (InvalidInputException e) {

            assertEquals("Could not create Date from 'invalid_date_string', invalid format.", e.getMessage());
        }
    }

    private MappingList createBookingMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(new ImportableBookingTitle("title"));
        mappingList.addMapping(new ImportablePriceValue("price"));
        mappingList.addMapping(new ImportablePriceType("price_type"));
        mappingList.addMapping(new ImportableBookingDate("date"));
        mappingList.addMapping(new ImportableCategoryTitle("category"));

        return mappingList;
    }

    private MappingList createAccountMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(new ImportableAccountTitle("account"));

        return mappingList;
    }
}
