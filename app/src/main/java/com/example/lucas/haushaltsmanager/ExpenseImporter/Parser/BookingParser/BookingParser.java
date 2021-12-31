package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.BookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingParser implements IParser<Booking> {
    public static final IRequiredField BOOKING_TITLE_KEY = new BookingTitle();

    private final PriceParser priceParser;
    private final DateParser dateParser;

    public BookingParser(PriceParser priceParser, DateParser dateParser) {
        this.priceParser = priceParser;
        this.dateParser = dateParser;
    }

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            add(BOOKING_TITLE_KEY);
            addAll(priceParser.getRequiredFields());
            addAll(dateParser.getRequiredFields());
        }};
    }

    @Override
    @NonNull
    public Booking parse(@NonNull Line line, @NonNull MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String bookingTitle = line.getAsString(mapping.getMappingForKey(BOOKING_TITLE_KEY));
        assertNotEmpty(bookingTitle);

        return new Booking(
                UUID.randomUUID(),
                bookingTitle,
                priceParser.parse(line, mapping),
                dateParser.parse(line, mapping),
                UUID.randomUUID(), // The Account Id will be set by the ImportStrategy
                UUID.randomUUID(), // The Category Id will be set by the ImportStrategy
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
