package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
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
import java.util.UUID;

public class BookingParser implements IParser<ExpenseObject> {
    public static final IRequiredField BOOKING_TITLE_KEY = new Title();

    private PriceParser priceParser;
    private CategoryParser categoryParser;
    private DateParser dateParser;

    public BookingParser(PriceParser priceParser, CategoryParser categoryParser, DateParser dateParser) {
        this.priceParser = priceParser;
        this.categoryParser = categoryParser;
        this.dateParser = dateParser;
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
                UUID.randomUUID(),
                bookingTitle,
                priceParser.parse(line, mapping),
                dateParser.parse(line, mapping),
                categoryParser.parse(line, mapping),
                "",
                UUID.randomUUID(),
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<ExpenseObject>()
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(ExpenseObject.class);
    }
}
