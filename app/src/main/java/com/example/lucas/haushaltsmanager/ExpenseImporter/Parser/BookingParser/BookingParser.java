package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;
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

public class BookingParser implements IParser<Booking> {
    public static final IRequiredField BOOKING_TITLE_KEY = new Title();

    private final PriceParser priceParser;
    private final DateParser dateParser;

    public BookingParser(PriceParser priceParser, DateParser dateParser) {
        this.priceParser = priceParser;
        this.dateParser = dateParser;
    }

    @Override
    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            add(BOOKING_TITLE_KEY);
            addAll(priceParser.getRequiredFields());
            addAll(dateParser.getRequiredFields());
        }};
    }

    public Booking parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String bookingTitle = line.getAsString(mapping.getMappingForKey(BOOKING_TITLE_KEY));
        assertNotEmpty(bookingTitle);

        return new Booking(
                UUID.randomUUID(),
                bookingTitle,
                priceParser.parse(line, mapping),
                dateParser.parse(line, mapping),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Booking.class);
    }
}
