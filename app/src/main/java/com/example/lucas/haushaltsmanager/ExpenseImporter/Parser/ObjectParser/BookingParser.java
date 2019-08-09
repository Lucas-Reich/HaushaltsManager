package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingParser implements IObjectParser<ExpenseObject> {
    public static final String BOOKING_TITLE_KEY = "booking_title";

    private Currency mainCurrency;

    public BookingParser(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    public ExpenseObject parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String bookingTitle = line.getAsString(mapping.getMappingForKey(BOOKING_TITLE_KEY));
        assertNotEmpty(bookingTitle);

        return new ExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                bookingTitle,
                createPrice(line, mapping),
                createDate(line, mapping),
                createCategory(line, mapping),
                "",
                ExpensesDbHelper.INVALID_INDEX,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mainCurrency
        );
    }

    private Price createPrice(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        PriceParser parser = new PriceParser(mainCurrency);

        return parser.parse(line, mapping);
    }

    private Category createCategory(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        CategoryParser parser = new CategoryParser();

        return parser.parse(line, mapping);
    }

    private Calendar createDate(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        DateParser parser = new DateParser();

        return parser.parse(line, mapping);
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(ExpenseObject.class);
    }
}
