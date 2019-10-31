package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.ArrayList;
import java.util.List;

public class BookingParser implements IParser<ExpenseObject> {
    public static final IRequiredField BOOKING_TITLE_KEY = new Title();

    private PriceParser priceParser;
    private CategoryParser categoryParser;
    private DateParser dateParser;
    private Currency mainCurrency;

    public BookingParser(PriceParser priceParser, CategoryParser categoryParser, DateParser dateParser, Currency mainCurrency) {
        this.priceParser = priceParser;
        this.categoryParser = categoryParser;
        this.dateParser = dateParser;

        this.mainCurrency = mainCurrency;
    }

    @Override
    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            add(BOOKING_TITLE_KEY);
            addAll(priceParser.getRequiredFields());
            addAll(categoryParser.getRequiredFields());
            addAll(dateParser.getRequiredFields());
        }};
    }

    public ExpenseObject parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String bookingTitle = line.getAsString(mapping.getMappingForKey(BOOKING_TITLE_KEY));
        assertNotEmpty(bookingTitle);

        return new ExpenseObject(
                ExpensesDbHelper.INVALID_INDEX,
                bookingTitle,
                priceParser.parse(line, mapping),
                dateParser.parse(line, mapping),
                categoryParser.parse(line, mapping),
                "",
                ExpensesDbHelper.INVALID_INDEX,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mainCurrency
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(ExpenseObject.class);
    }
}
